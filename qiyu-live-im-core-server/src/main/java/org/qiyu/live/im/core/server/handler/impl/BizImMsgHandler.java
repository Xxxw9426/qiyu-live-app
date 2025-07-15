package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: 默认的业务消息处理handler类
 * @Version: 1.0
 */

@Component
public class BizImMsgHandler implements SimpleHandler {


    private static final Logger LOGGER = LoggerFactory.getLogger(BizImMsgHandler.class);


    @Resource
    private MQProducer mqProducer;


    /***
     * 处理业务消息的逻辑
     * @param ctx
     * @param msg
     */
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg msg) {
        // 前期的参数校验
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            LOGGER.error("attr error, imMsgBody is {}", new String(msg.getBody()));
            // 有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr error");
        }
        byte[] body = msg.getBody();
        if (body == null || body.length == 0) {
            LOGGER.error("body error ,imMsgBody is {}", new String(msg.getBody()));
            return;
        }
        // 把消息传递给下游的业务服务
        Message message = new Message();
        message.setTopic(ImCoreServerProviderTopicNames.QIYU_LIVE_IM_BIZ_MSG_TOPIC);
        message.setBody(body);
        try {
            SendResult sendResult=mqProducer.send(message);
            LOGGER.info("[BizImMsgHandler]消息投递结果：{}",sendResult);
        } catch (Exception e) {
            LOGGER.error("send error,error is :",e);
            throw new RuntimeException(e);
        }
    }
}
