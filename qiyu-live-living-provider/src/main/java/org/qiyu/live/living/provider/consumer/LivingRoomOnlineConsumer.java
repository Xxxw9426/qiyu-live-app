package org.qiyu.live.living.provider.consumer;


import com.alibaba.fastjson.JSON;
import com.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-03
 * @Description: 消费RocketMQ中投递过来的有用户连接IM服务后的请求消息
 * @Version: 1.0
 */

@Component
public class LivingRoomOnlineConsumer implements InitializingBean {


    private static final Logger LOGGER= LoggerFactory.getLogger(LivingRoomOnlineConsumer.class);


    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;


    @Resource
    private ILivingRoomService livingRoomService;


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer defaultMQPushConsumer=new DefaultMQPushConsumer();
        defaultMQPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()+"-LivingRoomOnlineConsumer");
        defaultMQPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        defaultMQPushConsumer.setConsumeMessageBatchMaxSize(10);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听IM发送过来的业务消息topic
        defaultMQPushConsumer.subscribe(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC,"");
        defaultMQPushConsumer.setMessageListener((MessageListenerConcurrently)(msgs, context)->{
            for (MessageExt msg : msgs) {
                // 传入service层的方法中进行处理：将当前进入直播间的用户的id存入当前直播间在线用户的集合中
                livingRoomService.userOnlineHandler(JSON.parseObject(new String(msg.getBody()), ImOnlineDTO.class));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        defaultMQPushConsumer.start();
        LOGGER.info("mq消息消费者:LivingRoomOnlineConsumer启动，nameSrv is {}",rocketMQConsumerProperties.getNameSrv());
    }
}

