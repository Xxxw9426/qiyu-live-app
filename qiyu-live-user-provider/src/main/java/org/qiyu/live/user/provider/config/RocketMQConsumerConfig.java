package org.qiyu.live.user.provider.config;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.user.constants.CacheAsyncDeleteCode;
import org.qiyu.live.user.constants.UserProviderTopicNames;
import org.qiyu.live.user.dto.UserCacheAsyncDeleteDTO;
import org.qiyu.live.user.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: rocketMQ消费者的配置类，做rocketmq消费者的初始化操作等
 *                这个类会在spring容器初始化这个类bean的时候回调afterPropertiesSet()方法进行rocketMQ消费者的初始化
 * @Version: 1.0
 */

@Configuration
public class RocketMQConsumerConfig implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQConsumerConfig.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化RocketMQ的消费者
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + RocketMQConsumerConfig.class.getSimpleName());
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        mqPushConsumer.subscribe(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC,"");
        mqPushConsumer.setMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                // 当我们的rocketMQ监听到一次Redis缓存删除操作后，在这里进行第二次的延迟删除
                // 首先获取rocketMQ监听到的消息中的body的json字符串
                String msgStr=new String(msgs.get(0).getBody());
                // 将其转化为我们设置的统一的删除类
                UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = JSON.parseObject(msgStr, UserCacheAsyncDeleteDTO.class);
                // 判断当前延迟双删的业务场景，针对不同场景进行不同的操作
                // TODO 如果当前的业务场景是用户基础信息删除
                if(CacheAsyncDeleteCode.USER_INFO_DELETE.getCode()== userCacheAsyncDeleteDTO.getCode()) {
                    // 获取rocketMQ监听到的消息中我们设置的第二次删除操作需要用到的参数
                    Long userId=JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                    // 进行第二次删除操作
                    redisTemplate.delete(userProviderCacheKeyBuilder.buildUserInfoKey(userId));
                    LOGGER.info("延迟删除用户信息缓存，userId is {}",userId);
                // TODO 如果当前的业务场景时用户标签删除
                } else if(CacheAsyncDeleteCode.USER_TAG_DELETE.getCode()== userCacheAsyncDeleteDTO.getCode()) {
                    // 获取rocketMQ监听到的消息中我们设置的第二次删除操作需要用到的参数
                    Long userId=JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                    // 进行第二次删除操作
                    redisTemplate.delete(userProviderCacheKeyBuilder.buildTagKey(userId));
                    LOGGER.info("延迟删除用户标签缓存，userId is {}",userId);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        mqPushConsumer.start();
        LOGGER.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }

}
