package org.qiyu.live.living.provider.consumer;

import com.alibaba.fastjson.JSON;
import com.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import jakarta.annotation.Resource;
import org.apache.rocketmq.common.message.MessageExt;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-03
 * @Description: 消费RocketMQ中投递过来的有用户从IM服务器断连后的请求消息
 * @Version: 1.0
 */

@Component
public class LivingRoomOfflineConsumer implements InitializingBean {


    private static final Logger logger= LoggerFactory.getLogger(LivingRoomOfflineConsumer.class);


    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;


    @Resource
    private ILivingRoomService livingRoomService;


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer defaultMQPushConsumer=new DefaultMQPushConsumer();
        defaultMQPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()+"-LivingRoomOfflineConsumer");
        defaultMQPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        defaultMQPushConsumer.setConsumeMessageBatchMaxSize(10);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听IM发送过来的业务消息topic
        defaultMQPushConsumer.subscribe(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_BIZ_MSG_TOPIC,"");
        defaultMQPushConsumer.setMessageListener((MessageListenerConcurrently)(msgs, context)->{
            for (MessageExt msg : msgs) {
                // 传入service层的方法中进行处理：将当前退出直播间的用户的id从当前直播间在线用户的集合中移除
                livingRoomService.userOfflineHandler(JSON.parseObject(new String(msg.getBody()), ImOfflineDTO.class));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        defaultMQPushConsumer.start();
        logger.info("mq消息消费者:LivingRoomOfflineConsumer:启动，nameSrv is {}",rocketMQConsumerProperties.getNameSrv());
    }
}
