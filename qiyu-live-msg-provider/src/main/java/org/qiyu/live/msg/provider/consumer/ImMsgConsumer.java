package org.qiyu.live.msg.provider.consumer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.msg.provider.consumer.handler.MessageHandler;
import org.qiyu.live.msg.provider.consumer.handler.impl.SingleMessageHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: 消费RocketMQ中投递过来的IM服务的请求消息
 * @Version: 1.0
 */

@Component
public class ImMsgConsumer implements InitializingBean {


    private static final Logger LOGGER= LoggerFactory.getLogger(ImMsgConsumer.class);


    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;


    @Resource
    private MessageHandler singleMessageHandler;


    /***
     * TODO 监听IM服务的消息队列并且处理IM服务投递过来的请求消息
     *  消息投递流程：
     *    A发消息给B：(A)im-core-server --> msg-provider(进行一些业务逻辑处理）--> router(路由到B所在IM服务器地址) --> 通知到b --> im-core-server(B)
     *  实现思路：
     *    记录每个用户连接的IM服务器地址，然后根据消息接收者所在IM服务器的地址来向具体的机器发送消息转发请求
     *    转发请求的两种方法：
     *      1.基于MQ广播思路，可能会有消息风暴发生，100台机器，99%的mq消息都是无效的
     *      2.加入路由层，router中转的设计，router就是一个dubbo的rpc层
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer defaultMQPushConsumer=new DefaultMQPushConsumer();
        defaultMQPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName());
        defaultMQPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        // 一次性从broker中拉取10条消息到本地内存中进行消费
        defaultMQPushConsumer.setConsumeMessageBatchMaxSize(10);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听IM发送过来的业务消息topic
        defaultMQPushConsumer.subscribe(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_BIZ_MSG_TOPIC,"");
        defaultMQPushConsumer.setMessageListener((MessageListenerConcurrently)(msgs, context)->{
            // 前面设定了一次性拿出10条消息，这里对这10条消息的集合进行遍历，挨个处理
            for (MessageExt msg : msgs) {
                // 将IM服务中投递过来的mq消息中的消息体拿出来
                ImMsgBody imMsgBody = JSON.parseObject(msg.getBody(), ImMsgBody.class);
                // 然后传给当前服务的handler进行处理
                singleMessageHandler.onMsgReceive(imMsgBody);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        defaultMQPushConsumer.start();
        LOGGER.info("mq消息消费者启动，nameSrv is {}",rocketMQConsumerProperties.getNameSrv());
    }
}

