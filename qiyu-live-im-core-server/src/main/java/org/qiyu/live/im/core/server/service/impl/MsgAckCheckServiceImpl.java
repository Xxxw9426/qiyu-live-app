package org.qiyu.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.idea.qiyu.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.core.server.handler.impl.AckImMsgHandler;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-01
 * @Description: 操作消息发送后的ack消息的service接口实现类
 * @Version: 1.0
 */
@Service
public class MsgAckCheckServiceImpl implements IMsgAckCheckService {


    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Resource
    private MQProducer mqProducer;


    @Resource
    private ImCoreServerProviderCacheKeyBuilder imCoreServerProviderCacheKeyBuilder;


    private static final Logger LOGGER= LoggerFactory.getLogger(MsgAckCheckServiceImpl.class);


    /***
     * 用于当IM服务收到ACK消息后移除Redis中的ACK记录
     * @param imMsgBody
     */
    @Override
    public void doMsgAck(ImMsgBody imMsgBody) {
        redisTemplate.opsForHash().delete(imCoreServerProviderCacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(),imMsgBody.getAppId()),
                imMsgBody.getMsgId());
    }


    /**
     * 用于在IM服务转发了消息给客户端后向Redis中记录消息的ack和times
     * @param imMsgBody
     * @param times
     */
    @Override
    public void recordMsgAck(ImMsgBody imMsgBody, int times) {
        redisTemplate.opsForHash().put(imCoreServerProviderCacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(),imMsgBody.getAppId()),
                imMsgBody.getMsgId(),times);
    }


    /***
     * 发送延迟消息，用于进行消息重试功能
     * @param imMsgBody
     */
    @Override
    public void sendDelayMsg(ImMsgBody imMsgBody) {
        String jsonString = JSON.toJSONString(imMsgBody);
        Message message =new Message();
        message.setBody(jsonString.getBytes());
        message.setTopic(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_ACK_MSG_TOPIC);
        // 设置mq发送消息的延迟等级：等级1 -> 1秒  等级2 -> 5秒左右
        message.setDelayTimeLevel(2);
        try {
            // 发送延迟消息
            SendResult sendResult = mqProducer.send(message);
            LOGGER.info("[MsgAckCheckServiceImpl] msg is {} ,sendResult is {}",jsonString,sendResult);
        } catch (Exception e) {
            LOGGER.error("[MsgAckCheckServiceImpl] error is",e);
        }

    }


    /***
     * 获取ack消息的重试次数
     * @param msgId
     * @param userId
     * @param appId
     * @return
     */
    @Override
    public int getMsgAckTimes(String msgId, long userId, int appId) {
        Object value=redisTemplate.opsForHash().get(imCoreServerProviderCacheKeyBuilder.buildImAckMapKey(userId,appId),msgId);
        if(value==null){
            return -1;
        }
        return (int)value;
    }
}
