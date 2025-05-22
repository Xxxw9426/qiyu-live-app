package org.qiyu.live.user.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.user.constants.CacheAsyncDeleteCode;
import org.qiyu.live.user.constants.UserProviderTopicNames;
import org.qiyu.live.user.dto.UserCacheAsyncDeleteDTO;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserMapper;
import org.qiyu.live.user.provider.dao.po.UserPO;
import org.qiyu.live.user.provider.service.IUserService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-04-29
 * @Description: 用户模块service层实现类
 * @Version: 1.0
 */

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserMapper userMapper;

    @Resource
    private RedisTemplate<String, UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Resource
    private MQProducer mqProducer;

    /***
     * 根据userId查询UserDTO对象
     * @param userId
     * @return
     */
    @Override
    public UserDTO getByUserId(Long userId) {
        if(userId==null) {
            return null;
        }
        String key=userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        UserDTO userDTO=redisTemplate.opsForValue().get(key);
        // 如果从缓存中查询到数据的话，直接返回缓存中的数据
        if(userDTO!=null) {
            return userDTO;
        }
        // 没有从缓存中查询到数据的话就从数据库查询，并且将查询到的数据写入Redis
        userDTO=ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
        if(userDTO!=null) {
            redisTemplate.opsForValue().set(key,userDTO,30,TimeUnit.MINUTES);
        }
        // 最终返回
        return userDTO;
    }


    /***
     * 更新用户资料
     * @param userDTO
     * @return
     */
    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if(userDTO==null || userDTO.getUserId()==null) {
            return false;
        }
        int updateStatus=userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        if(updateStatus > -1) {
            // 更新完数据库后进行缓存的删除
            String key=userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
            redisTemplate.delete(key);
            // 向rocketMQ发送消息，通过RocketMQ实现延迟双删中的第二次删除操作
            // 设置删除操作的删除信息对象
            UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = new UserCacheAsyncDeleteDTO();
            // 设置当前延迟双删的业务场景code为用户标签删除场景
            userCacheAsyncDeleteDTO.setCode(CacheAsyncDeleteCode.USER_INFO_DELETE.getCode());
            // 设置延迟双删时会用到的参数：以map形式存储参数并且转化为json字符串
            Map<String,Object> jsonParam = new HashMap<>();
            jsonParam.put("userId",userDTO.getUserId());
            userCacheAsyncDeleteDTO.setJson(JSON.toJSONString(jsonParam));
            // 设置要向rocketMQ发送的消息
            Message message = new Message();
            // 发送给rocketMQ的消息中的内容为信息变更的用户的信息
            message.setBody(JSON.toJSONString(userCacheAsyncDeleteDTO).getBytes());
            message.setTopic(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC);
            // 延迟消息级别设置，1代表1秒钟左右，当rocketMQ接收到这条消息后就会根据这里设置的时间进行第二次删除操作指令的发布
            message.setDelayTimeLevel(1);
            try {
                mqProducer.send(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }


    /***
     * 插入用户信息
     * @param userDTO
     * @return
     */
    @Override
    public boolean insertOne(UserDTO userDTO) {
        if(userDTO==null || userDTO.getUserId()==null) {
            return false;
        }
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }


    /***
     * 批量查询用户信息
     * @param userIdList
     * @return
     */
    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        // 首先判断要批量查询的用户集合是否为空
        if(CollectionUtils.isEmpty(userIdList)) {
            return Maps.newHashMap();
        }
        // 对集合中的id进行过滤
        userIdList=userIdList.stream().filter(id->id>10000).collect(Collectors.toList());
        // 过滤后再次判断
        if(CollectionUtils.isEmpty(userIdList)) {
            return Maps.newHashMap();
        }
        // 在这里如果直接到数据库中进行查询会有两个问题：
        // 1. 导致性能不太好
        // 2. 直接从数据库进行查询时其底层会通过ShardingJdbc进行union all 语句查询，也会导致查询效率降低
        // TODO 虽然方案一解决了这个问题，但是开启了多线程会对java程序堆空间的要求较高，因此我们可以使用Redis继续优化
        //      我们使用Redis中的multiGet()方法，这个方法允许一次性传入多个key后使用一次io返回查询到的所有结果。
        List<String> keyList=new ArrayList<>();
        // 构造在Redis中要进行查询的key值集合
        userIdList.forEach(userId->{
            keyList.add(userProviderCacheKeyBuilder.buildUserInfoKey(userId));
        });
        List<UserDTO> userList = redisTemplate.opsForValue().multiGet(keyList).stream().filter(x -> x != null).collect(Collectors.toList());
        // 所有请求都命中了缓存
        if (!CollectionUtils.isEmpty(userList) && userList.size() == userIdList.size()) {
            return userList.stream().collect(Collectors.toMap(UserDTO::getUserId, x -> x));
        }
        // 获取在缓存中的用户的集合
        List<Long> userIdInCacheList = userList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        // 获取不在缓存中的用户的集合
        List<Long> userIdNotInCacheList = userIdList.stream().filter(x -> !userIdInCacheList.contains(x)).collect(Collectors.toList());


        // TODO 解决方案一：采用多线程加本地内存聚合的方式来替换原本的union all的方式完成分表的查询
        // 首先将所有用户id根据其所在的表进行分组
        Map<Long, List<Long>> userIdMap = userIdNotInCacheList.stream().collect(Collectors.groupingBy(userId -> userId % 100));
        // 根据不同的组的分类进行遍历
        List<UserDTO> userDTOList=new CopyOnWriteArrayList<>();
        // parallelStream()表明采用的是并行流的方式进行遍历与查询
        userIdMap.values().parallelStream().forEach(queryUserIdList ->{
            userDTOList.addAll(ConvertBeanUtils.convertList(userMapper.selectBatchIds(queryUserIdList), UserDTO.class));
        });
        if (!CollectionUtils.isEmpty(userDTOList)) {
            // 将刚才从数据库中查询出来的数据存入map中准备写入Redis
            Map<String, UserDTO> saveCacheMap = userDTOList.stream().
                    collect(Collectors.toMap(userDto -> userProviderCacheKeyBuilder.buildUserInfoKey(userDto.getUserId()), x -> x));
            redisTemplate.opsForValue().multiSet(saveCacheMap);
            // 对命令执行批量过期设置操作
            // 使用管道的方法，管道批量传输命令，减少网络IO开销
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String redisKey : saveCacheMap.keySet()) {
                        operations.expire((K) redisKey,createRandomTime() ,TimeUnit.SECONDS);
                    }
                    return null;
                }
            });
            userList.addAll(userDTOList);
        }
        return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId,x->x));
    }


    /**
     * 创建随机的过期时间 用于redis设置key过期
     * @return
     */
    private int createRandomTime() {
        int randomNumSecond = ThreadLocalRandom.current().nextInt(10000);
        return randomNumSecond + 30 * 60;
    }
}
