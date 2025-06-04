package org.qiyu.live.im.core.server.consumer;

import com.alibaba.fastjson.JSON;
import com.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
import org.qiyu.live.im.core.server.service.IRouterHandlerService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-01
 * @Description: 监听Im服务中延迟消息校验ack的consumer类
 * @Version: 1.0
 */
@Component
public class ImAckConsumer implements InitializingBean {


    private static final Logger logger= LoggerFactory.getLogger(ImAckConsumer.class);


    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;


    @Resource
    private IMsgAckCheckService msgAckCheckService;


    @Resource
    private IRouterHandlerService routerHandlerService;


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer defaultMQPushConsumer=new DefaultMQPushConsumer();
        // 声明消费组
        defaultMQPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()+"_"+ImAckConsumer.class.getSimpleName());
        // 设置nameSrv的地址
        defaultMQPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        // 设置每次拉取一条消息
        defaultMQPushConsumer.setConsumeMessageBatchMaxSize(1);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 定义我们要监听的topic
        defaultMQPushConsumer.subscribe(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_ACK_MSG_TOPIC,"");
        // 处理我们监听到的消息的逻辑
        defaultMQPushConsumer.setMessageListener((MessageListenerConcurrently)(msgs, context)->{
            String json=new String(msgs.get(0).getBody());
            ImMsgBody imMsgBody = JSON.parseObject(json, ImMsgBody.class);
            // 获取当前这条消息的重试次数
            int retryTimes = msgAckCheckService.getMsgAckTimes(imMsgBody.getMsgId(), imMsgBody.getUserId(), imMsgBody.getAppId());
            logger.info("[ImAckConsumer] retryTimes: {}，msgId is {}",retryTimes, imMsgBody.getMsgId());
            // 之前我们设置了如果当前消息被移除则返回重试次数为-1
            if(retryTimes<0) {
                // 重试次数小于0说明当前消息已经成功被客户端收到，并且客户端已经向Im服务发送了ack消息
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            if(retryTimes <2) {
                // 只支持重发一次
                // 重发消息，增加重试次数
                msgAckCheckService.recordMsgAck(imMsgBody,retryTimes+1);
                routerHandlerService.sendMsgToClient(imMsgBody);
                // 发送延迟消息判断
                msgAckCheckService.sendDelayMsg(imMsgBody);
            } else {
                // 超过重发次数，不再重发，移除缓存中的数据
                msgAckCheckService.doMsgAck(imMsgBody);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        defaultMQPushConsumer.start();
        logger.info("mq消息消费者启动，nameSrv is {}",rocketMQConsumerProperties.getNameSrv());
    }
}
