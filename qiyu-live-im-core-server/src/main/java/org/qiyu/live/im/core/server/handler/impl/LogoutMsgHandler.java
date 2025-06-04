package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.constants.ImConstants;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.qiyu.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.qiyu.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.qiyu.live.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: 登出消息的处理handler类
 * @Version: 1.0
 */

@Component
public class LogoutMsgHandler implements SimpleHandler {


    private static final Logger LOGGER= LoggerFactory.getLogger(LogoutMsgHandler.class);


    @Resource
    private MQProducer mqProducer;


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /***
     * 处理登出的逻辑
     * @param ctx
     * @param msg
     */
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg msg) {
        // TODO 以上只是理想情况下时，客户端断线的时候会向Im服务端发送一个断线消息包
        //  但是还有可能就是客户端因为网络原因突然断线，没来得及发送断线消息包就下线了，这种情况下用户的channel就永远没有办法删除，最终导致内存泄漏
        Long userId= ImContextUtils.getUserId(ctx);
        Integer appId=ImContextUtils.getAppId(ctx);
        // 如果获取到的userId为空，直接不处理
        if (userId == null || appId == null) {
            LOGGER.error("attr error, imMsgBody is {}", new String(msg.getBody()));
            // 有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr error");
        }
        // 将im消息回写给客户端并且关闭连接
        logoutHandler(ctx,userId,appId);
    }


    /***
     * 用户登出参数校验完成后将im消息回写给客户端并且关闭连接
     * @param ctx
     * @param userId
     * @param appId
     */
    private void logoutHandler(ChannelHandlerContext ctx, Long userId,Integer appId) {
        ImMsgBody respBody=new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("true");
        ImMsg respMsg=ImMsg.build(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(respBody));
        ctx.writeAndFlush(respMsg);
        LOGGER.info("[LogoutMsgHandler] logout success, userId is {}, appId is {}", userId, appId);
        // 不为空的话则将当前userId对应的channel信息从map中移除
        ChannelHandlerContextCache.remove(userId);
        // 用户登出成功后
        stringRedisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId);
        // 发送MQ消息删除当前用户与直播间的关联信息
        sendLogoutMQ(ctx,userId,appId);
        ImContextUtils.removeUserId(ctx);
        ImContextUtils.removeAppId(ctx);
        ctx.close();
    }


    /***
     * 当用户退出登录成功后发送MQ消息实现将当前当前用户id从直播间的人员集合中移除
     * @param ctx
     * @param userId
     * @param appId
     */
    private void sendLogoutMQ(ChannelHandlerContext ctx,Long userId,Integer appId) {
        // 设置要发送的数据
        ImOfflineDTO imOfflineDTO=new ImOfflineDTO();
        imOfflineDTO.setUserId(userId);
        imOfflineDTO.setAppId(appId);
        imOfflineDTO.setRoomId(ImContextUtils.getRoomId(ctx));
        imOfflineDTO.setLogoutTime(System.currentTimeMillis());
        Message message=new Message();
        // 设置mq发送的主题为用户上线
        message.setTopic(ImCoreServerProviderTopicNames.IM_OFFLINE_TOPIC);
        message.setBody(JSON.toJSONString(imOfflineDTO).getBytes());
        try {
            // 发送
            SendResult sendResult = mqProducer.send(message);
            LOGGER.info("[sendLogoutMQ] sendResult is {}", sendResult);
        } catch (Exception e) {
            LOGGER.error("[sendLogoutMQ] error is",e);
        }
    }
}
