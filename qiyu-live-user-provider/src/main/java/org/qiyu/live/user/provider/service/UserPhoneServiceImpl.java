package org.qiyu.live.user.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.common.interfaces.utils.DESUtils;
import org.qiyu.live.id.generate.enums.IdTypeEnum;
import org.qiyu.live.id.generate.interfaces.IdGenerateRpc;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.dto.UserLoginDTO;
import org.qiyu.live.user.dto.UserPhoneDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserPhoneMapper;
import org.qiyu.live.user.provider.dao.po.UserPhonePO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-13
 * @Description: 用户电话相关服务service接口实现类
 * @Version: 1.0
 */

@Service
public class UserPhoneServiceImpl implements IUserPhoneService {


    @Resource
    private IUserPhoneMapper userPhoneMapper;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;


    @Resource
    private IUserService userService;


    @DubboReference
    private IdGenerateRpc idGenerateRpc;


    /***
     * 用户登录(未注册过则注册初始化)
     * 返回注册成功后的userId和token 或者登录失败的失败信息
     * @param phone
     * @return
     */
    @Override
    public UserLoginDTO login(String phone) {
        // TODO 用户登录(注册)流程
        //  1. 校验参数
        //  2. 判断是否注册过
        //  3. 注册过的话创建token返回userId
        //  4. 没有注册过的话，生成user信息，插入电话记录并且绑定userId
        // 参数校验
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        // 判断是否注册过
        UserPhoneDTO userPhone = queryByPhone(phone);
        // 如果当前用户已经注册过
        if(userPhone!=null) {
            // 返回当前用户的userId和token
            return UserLoginDTO.loginSuccess(userPhone.getUserId(),"");
        }
        // 没有注册过的话，生成user信息，插入电话记录并且绑定userId
        return registerAndLogin(phone);
    }


    /***
     * 根据用户手机号码查询用户电话关联用户信息(加入了缓存和缓存击穿的逻辑)
     * @param phone
     * @return
     */
    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        // 如果参数为空，返回查询结果为空
        if(StringUtils.isEmpty(phone)) {
            return null;
        }
        // 首先从redis查询当前phone对应的用户信息
        String redisKey=userProviderCacheKeyBuilder.buildUserPhoneObjKey(phone);
        UserPhoneDTO userPhoneDTO = (UserPhoneDTO) redisTemplate.opsForValue().get(redisKey);
        // 如果缓存命中则直接返回
        if(userPhoneDTO!=null) {
            // 判断命中的缓存是数据还是我们设置的空值
            if(userPhoneDTO.getUserId()==null) {
                return null;
            }
            return userPhoneDTO;
        }
        // 缓存未命中则从数据库查询
        userPhoneDTO= this.queryByPhoneFromDB(phone);
        // 从数据库中查询到数据
        if(userPhoneDTO!=null) {
            // 解密电话号码
            userPhoneDTO.setPhone(DESUtils.decrypt(userPhoneDTO.getPhone()));
            // 写入缓存并返回
            redisTemplate.opsForValue().set(redisKey,userPhoneDTO,30, TimeUnit.MINUTES);
            return userPhoneDTO;
        }
        // 缓存空值避免缓存击穿
        userPhoneDTO=new UserPhoneDTO();
        redisTemplate.opsForValue().set(redisKey,userPhoneDTO,5, TimeUnit.MINUTES);
        return null;
    }


    /***
     * 根据用户id批量查询用户的电话关联用户信息
     * @param userId
     * @return
     */
    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        // 首先进行参数校验
        if(userId==null || userId<10000) {
            return Collections.emptyList();
        }
        // 先从缓存中进行查询
        String redisKey=userProviderCacheKeyBuilder.buildUserPhoneListKey(userId);
        List<Object> userPhoneList=redisTemplate.opsForList().range(redisKey,0,-1);
        // 如果命中缓存的话直接返回
        if(!CollectionUtils.isEmpty(userPhoneList)) {
            // 如果命中的是我们缓存的空值，则返回空
            if(((UserPhoneDTO)userPhoneList.get(0)).getUserId()==null) {
                return Collections.emptyList();
            }
            // 否则返回我们命中的结果
            return userPhoneList.stream().map(x->(UserPhoneDTO) x).collect(Collectors.toList());
        }
        // 缓存未命中则从数据库进行查询
        List<UserPhoneDTO> userPhoneDTOS = this.queryByUserIdFromDB(userId);
        // 如果数据库中查询到了数据,则将查询到数据写入缓存并且直接返回
        if(!CollectionUtils.isEmpty(userPhoneDTOS)) {
            userPhoneDTOS.stream().forEach(x -> x.setPhone(DESUtils.decrypt(x.getPhone())));
            redisTemplate.opsForList().leftPushAll(redisKey,userPhoneDTOS.toArray());
            redisTemplate.expire(redisKey,30, TimeUnit.MINUTES);
            return userPhoneDTOS;
        }
        // 数据库中没有查询到数据：缓存空值避免缓存击穿
        redisTemplate.opsForList().leftPushAll(redisKey,new UserPhoneDTO());
        redisTemplate.expire(redisKey,5, TimeUnit.MINUTES);
        return Collections.emptyList();
    }


    /***
     * 用户登录过程中未注册时的注册方法
     * 在这个方法中首先要向 t_user表中插入用户信息
     * 要向 t_user表中插入用户就需要用到我们之前设置好的多线程分布式全局id生成器，并且调用IUserService接口中的插入用户的方法插入用户
     * 然后向 t_user_phone表中插入用户和用户电话关联信息
     * @param phone
     * @return
     */
    private UserLoginDTO registerAndLogin(String phone) {

        // 首先向 t_user表中插入用户信息
        // 调用多线程分布式全局id生成器为当前用户生成id
        Long userId=idGenerateRpc.getUnSeqId(IdTypeEnum.USER_ID.getCode());
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName("旗鱼用户-" + userId);
        userService.insertOne(userDTO);
        // 然后向 t_user_phone表中插入用户和用户电话关联信息
        UserPhonePO userPhonePO = new UserPhonePO();
        userPhonePO.setUserId(userId);
        userPhonePO.setPhone(DESUtils.encrypt(phone));
        userPhonePO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        userPhoneMapper.insert(userPhonePO);
        // 用户注册成功后将之前判断用户是否注册过时Redis中缓存的当前用户的电话用户信息的空值删除
        redisTemplate.delete(userProviderCacheKeyBuilder.buildUserPhoneObjKey(phone));
        // 两个都插入成功后返回注册并且登录成功的信息
        return UserLoginDTO.loginSuccess(userId,null);
    }


    /***
     * 在用户登录注册过程中生成和保存token的方法
     * @param userId
     * @return
     */
    private String createAndSaveLoginToken(Long userId) {
        String token = UUID.randomUUID().toString();
        String redisKey = userProviderCacheKeyBuilder.buildUserLoginTokenKey(token);
        redisTemplate.opsForValue().set(redisKey, userId, 30L, TimeUnit.DAYS);
        return token;
    }


    /***
     *  真正从数据库中根据用户手机号码查询用户电话关联用户信息的业务方法
     * @param phone
     * @return
     */
    private UserPhoneDTO queryByPhoneFromDB(String phone) {
        LambdaQueryWrapper<UserPhonePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPhonePO::getPhone, DESUtils.encrypt(phone));
        wrapper.eq(UserPhonePO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        wrapper.last("limit 1");
        return ConvertBeanUtils.convert(userPhoneMapper.selectOne(wrapper), UserPhoneDTO.class);
    }


    /***
     * 真正从数据库中根据用户id查询用户电话关联用户信息的业务方法
     * @return
     */
    private List<UserPhoneDTO> queryByUserIdFromDB(Long userId) {
        LambdaQueryWrapper<UserPhonePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPhonePO::getUserId, userId);
        wrapper.eq(UserPhonePO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        wrapper.last("limit 1");
        return ConvertBeanUtils.convertList(userPhoneMapper.selectList(wrapper), UserPhoneDTO.class);
    }
}


