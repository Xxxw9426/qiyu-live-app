package org.qiyu.live.gift.provider.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.qiyu.live.common.interfaces.topic.LivingRoomTopicNames;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-19
 * @Description: 接收到直播开始的MQ消息后的消息处理类：在这里会调用加载商品库存的缓存到Redis的方法
 * @Version: 1.0
 */
@Configuration
public class StartLivingRoomConsumer implements InitializingBean {


    private static final Logger logger= LoggerFactory.getLogger(StartLivingRoomConsumer.class);


    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;


    @Resource
    private ISkuStockInfoRpc skuStockInfoRPC;


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer();
        //老版本中会开启，新版本的mq不需要使用到
        consumer.setVipChannelEnabled(false);
        consumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        consumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()+"_"+ StartLivingRoomConsumer.class.getSimpleName());
        //一次从broker中拉取10条消息到本地内存当中进行消费
        consumer.setConsumeMessageBatchMaxSize(10);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听礼物缓存数据跟更新的行为
        consumer.subscribe(LivingRoomTopicNames.START_LIVING_ROOM,"");
        consumer.setMessageListener((MessageListenerConcurrently)(msg, context)->{
            for (MessageExt messageExt : msg) {
                JSONObject jsonObject = JSON.parseObject(new String(messageExt.getBody()));
                Long anchorId = jsonObject.getLong("anchorId");
                logger.info("[GiftConfigCacheConsumer] remove gift cache");
                skuStockInfoRPC.prepareStockInfo(anchorId);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        logger.info("mq消费者:StartLivingRoomConsumer,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }
}
