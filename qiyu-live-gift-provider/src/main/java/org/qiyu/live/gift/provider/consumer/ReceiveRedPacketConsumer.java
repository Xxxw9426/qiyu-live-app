package org.qiyu.live.gift.provider.consumer;

import com.alibaba.fastjson.JSON;
import com.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.gift.bo.SendRedPacketBO;
import org.qiyu.live.gift.provider.service.IRedPacketConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: todo 用户领取到红包后异步MQ消息的消费类
 *                  当用户在页面中通过点击领取到红包并且向后端进行了领取红包的请求后，我们要进行一些响应数据的记录和用户账户的更新
 *                    记录当前用户在本次抢红包活动中总共领取到的红包金额
 *                    记录总共分发的红包数量
 *                    记录总共分发的红包金额
 *                    记录本次红包雨活动中被抢到的红包的最大金额
 *                    更新用户账户
 *
 * @Version: 1.0
 */
@Component
public class ReceiveRedPacketConsumer implements InitializingBean{


    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveRedPacketConsumer.class);


    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;


    @Resource
    private IRedPacketConfigService redPacketConfigService;


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + ReceiveRedPacketConsumer.class.getSimpleName());
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听礼物缓存数据更新的行为
        mqPushConsumer.subscribe(GiftProviderTopicNames.RECEIVE_RED_PACKET, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            try {
                SendRedPacketBO sendRedPacketBO = JSON.parseObject(msgs.get(0).getBody(), SendRedPacketBO.class);
                redPacketConfigService.receiveRedPacketHandle(sendRedPacketBO.getReqDTO(), sendRedPacketBO.getPrice());
            }catch (Exception e) {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        LOGGER.info("【ReceiveRedPacketConsumer】mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }

}
