package org.qiyu.live.user.provider.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.user.constants.CacheAsyncDeleteCode;
import org.qiyu.live.user.constants.UserProviderTopicNames;
import org.qiyu.live.user.constants.UserTagFieldNameConstants;
import org.qiyu.live.user.constants.UserTagsEnum;
import org.qiyu.live.user.dto.UserCacheAsyncDeleteDTO;
import org.qiyu.live.user.dto.UserTagDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserTagMapper;
import org.qiyu.live.user.provider.dao.po.UserTagPO;
import org.qiyu.live.user.provider.service.IUserTagService;
import org.qiyu.live.user.utils.TagInfoUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-06
 * @Description: 用户标签service接口实现类
 * @Version: 1.0
 */

@Service
public class UserTagServiceImpl implements IUserTagService {


    @Resource
    private IUserTagMapper userTagMapper;


    @Resource
    private RedisTemplate<String, UserTagDTO> redisTemplate;


    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;


    @Resource
    private MQProducer mqProducer;


    /**
     * 设置标签
     * @param userId
     * @param userTagsEnum
     * @return
     */
    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {

        // 首先尝试update，如果update成功的话则直接返回true
        // update失败有两种情况：
        // 1. 设置了标签但是update失败
        // 2. 数据库中没有当前userId对应的数据
        // 我们可以通过select当前数据库中userId对应的数据，如果查询到的话说明当前标签已经设置过了，直接返回
        // 如果没有查询到说明在此之前数据库中还没有存在过当前userId对应的数据，因此我们首先执行insert操作，然后再进行update

        /***
         *  setTag方法的参数：
         *  @param userId  设置标签的用户id
         *  @param userTagsEnum.getFieldName()   要修改的字段
         *  @param userTagsEnum.getTag()     要设置的对应标签的值
         */
        // 首先尝试执行update操作
        boolean updateStatus=userTagMapper.setTag(userId,userTagsEnum.getFieldName(),userTagsEnum.getTag()) > 0;
        // 更新成功
        if(updateStatus){
            // 删除Redis中当前用户标签数据对象的缓存
            deleteUserTagDTOFromRedis(userId);
            return true;
        }
        // 更新失败
        // 从redis拿取锁进行对数据库进行操作
        String setNxKey = cacheKeyBuilder.buildTagLockKey(userId);
        // TODO 当用户数据在数据库中不存在时，我们通过使用redis中的setnx来实现只允许单线程和进程执行接下来的操作
        //      这里的实现方式如下，但是这种方式不具有原子性，因此我们使用redisTemplate中的execute方法来实现原子性操作。
        //  redisTemplate.opsForValue().setIfAbsent();
        //  redisTemplate.expire();
        String setNxResult= (String) redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                // 获得redis的序列化器和反序列化器
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                // 这行对redis的操作同时执行了setnx的功能歌设置过期时间的功能
                // TODO 这里我们设置过期时间为3秒，三秒后如果锁没有被主动释放则会触发被动释放
                return (String) connection.execute("set", keySerializer.serialize(setNxKey),
                        valueSerializer.serialize("-1"),
                        "NX".getBytes(StandardCharsets.UTF_8),
                        "EX".getBytes(StandardCharsets.UTF_8),
                        "3".getBytes(StandardCharsets.UTF_8));
            }
        });
        // 上面的redis的execute()方法执行成功将返回OK，返回的不是OK则说明获取锁失败，直接返回失败
        if (!"OK".equals(setNxResult)) {
            return false;
        }
        // 查询数据库中是否有我们要更新的对象来判断更新失败的原因
        UserTagPO userTagPO=userTagMapper.selectById(userId);
        // 数据库中有我们要更新的对象，返回更新失败
        if(userTagPO!=null){
            return false;
        }
        // 数据库中没有我们要更新的对象，插入我们要更新的对象并且进行更新操作
        userTagPO=new UserTagPO();
        userTagPO.setUserId(userId);
        userTagMapper.insert(userTagPO);
        updateStatus=userTagMapper.setTag(userId,userTagsEnum.getFieldName(),userTagsEnum.getTag()) > 0;
        // 释放锁
        redisTemplate.delete(setNxKey);
        return updateStatus;
    }


    /**
     * 取消标签
     * @param userId
     * @param userTagsEnum
     * @return
     */
    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {

        /***
         *  cancelTag方法的参数：
         *  @param userId  设置标签的用户id
         *  @param userTagsEnum.getFieldName()   要修改的字段
         *  @param userTagsEnum.getTag()     要设置的对应标签的值
         */
        boolean cancelStatus=userTagMapper.cancelTag(userId,userTagsEnum.getFieldName(),userTagsEnum.getTag()) > 0 ;
        // 如果删除失败的话直接返回错误
        if(!cancelStatus){
            return false;
        }
        // 如果删除成功的话则删除缓存中的数据
        deleteUserTagDTOFromRedis(userId);
        return true;
    }


    /**
     * 是否包含某个标签
     * 判断方法：
     *    我们将从数据库取出来要判断的标签所在字段的值与我们当前标签的值进行与运算，
     *    如果当前标签的运算结果与运算前一样则说明包含，反之则不包含。
     * @param userId
     * @param userTagsEnum
     * @return
     */
    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {

        // 首先查询缓存中用户标签数据对象
        UserTagDTO userTagDTO = this.queryByUserIdFromRedis(userId);
        // 如果缓存未命中并且数据库也未命中
        if(userTagDTO ==null) {
            return false;
        }
        // 缓存命中或者数据库命中
        String queryFieldName = userTagsEnum.getFieldName();
        // 根据用户标签数据对象做相应的判断
        // 如果当前要判断的标签存储在tag_info_01字段属性中
        if(UserTagFieldNameConstants.TAG_INFO_01.equals(queryFieldName)) {
            // 传入当前字段的值和要判断的标签的值进行查询
            return TagInfoUtils.isContain(userTagDTO.getTagInfo01(),userTagsEnum.getTag());
        } else if(UserTagFieldNameConstants.TAG_INFO_02.equals(queryFieldName)) {
            // 传入当前字段的值和要判断的标签的值进行查询
            return TagInfoUtils.isContain(userTagDTO.getTagInfo02(),userTagsEnum.getTag());
        } else if(UserTagFieldNameConstants.TAG_INFO_03.equals(queryFieldName)) {
            // 传入当前字段的值和要判断的标签的值进行查询
            return TagInfoUtils.isContain(userTagDTO.getTagInfo03(),userTagsEnum.getTag());
        }
        return false;
    }


    /***
     * 在查询用户标签前首先在Redis缓存中查询用户标签的对象的方法
     * @param userId
     * @return
     */
    private UserTagDTO queryByUserIdFromRedis(Long userId) {
        // 从缓存中查询当前用户的用户标签数据对象
        String redisKey = cacheKeyBuilder.buildTagKey(userId);
        UserTagDTO userTagDTO = redisTemplate.opsForValue().get(redisKey);
        // 如果缓存命中，直接返回
        if(userTagDTO!=null) {
            return userTagDTO;
        }
        // 未命中
        // 从查询数据库中用户标签数据对象
        UserTagPO userTagPO = userTagMapper.selectById(userId);
        // 没有查到，返回空
        if(userTagPO ==null) {
            return null;
        }
        // 查到的话返回查到的对象并且写入redis
        userTagDTO= ConvertBeanUtils.convert(userTagPO, UserTagDTO.class);
        redisTemplate.opsForValue().set(redisKey,userTagDTO);
        redisTemplate.expire(redisKey,30, TimeUnit.MINUTES);
        return userTagDTO;
    }


    /***
     * 以延迟双删的形式删除Redis缓存中当前的用户标签数据对象
     * @param userId
     */
    private void deleteUserTagDTOFromRedis(Long userId) {
        String redisKey = cacheKeyBuilder.buildTagKey(userId);
        redisTemplate.delete(redisKey);
        // 向rocketMQ发送消息，通过RocketMQ实现延迟双删中的第二次删除操作
        // 设置删除操作的删除信息对象
        UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = new UserCacheAsyncDeleteDTO();
        // 设置当前延迟双删的业务场景code为用户标签删除场景
        userCacheAsyncDeleteDTO.setCode(CacheAsyncDeleteCode.USER_TAG_DELETE.getCode());
        // 设置延迟双删时会用到的参数：以map形式存储参数并且转化为json字符串
        Map<String,Object> jsonParam = new HashMap<>();
        jsonParam.put("userId",userId);
        userCacheAsyncDeleteDTO.setJson(JSON.toJSONString(jsonParam));
        // 设置要向rocketMQ发送的消息
        Message message = new Message();
        // 发送给rocketMQ的消息中的内容为用户标签发生变化的用户id
        message.setBody(JSON.toJSONString(userCacheAsyncDeleteDTO).getBytes());
        message.setTopic(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC);
        // 延迟消息级别设置，1代表1秒钟左右，当rocketMQ接收到这条消息后就会根据这里设置的时间进行第二次删除操作指令的发布
        message.setDelayTimeLevel(1);
        // 发送给rocketMQ
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
