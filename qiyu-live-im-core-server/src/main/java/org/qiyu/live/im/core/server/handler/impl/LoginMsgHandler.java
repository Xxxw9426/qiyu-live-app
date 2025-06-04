package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.qiyu.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.constants.ImConstants;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImContextAttr;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.qiyu.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: 登录消息的处理handler类
 * @Version: 1.0
 */

@Component
public class LoginMsgHandler implements SimpleHandler {


    private static final Logger LOGGER= LoggerFactory.getLogger(LoginMsgHandler.class);


    @DubboReference
    private ImTokenRpc imTokenRpc;


    @Resource
    private MQProducer mqProducer;


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /***
     * 处理登录的逻辑
     * @param ctx
     * @param msg
     */
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg msg) {
        // 获得登录消息
        byte[] body = msg.getBody();
        // 校验参数
        if(body == null || body.length == 0){
            ctx.close();
            LOGGER.error("body error,imMsg is {}",msg);
            throw new IllegalArgumentException("body error");
        }
        // 获得登录消息体的内容
        ImMsgBody imMsgBody= JSON.parseObject(new String(body), ImMsgBody.class);
        // 对消息体中的userId和appId进行校验
        Long userIdFromMsg = imMsgBody.getUserId();
        int appId = imMsgBody.getAppId();
        // 取出token进行校验
        String token = imMsgBody.getToken();
        // 校验上面取出来的三个参数
        if(StringUtils.isEmpty(token) || userIdFromMsg < 10000 || appId < 10000) {
            ctx.close();
            LOGGER.error("param error,imMsg is {}",msg);
            throw new IllegalArgumentException("param error");
        }
        // 校验成功后调用im服务中的token校验rpc接口校验token
        Long userId=imTokenRpc.getUserIdByToken(token);
        // 如果获取到的userId与当前用户的相同
        // 用户登录成功，建立连接
        if(userId !=null && userId.equals(userIdFromMsg)) {
            // 调用建立连接的方法
            loginSuccessHandler(ctx, userId, appId,null);
            return;
        }
        // 不允许建立连接
        ctx.close();
        LOGGER.error("token error, imMsgBody is {}", new String(msg.getBody()));
        throw new IllegalArgumentException("token check error");
    }


    /***
     * 当用户参数校验通过登录成功后建立连接的方法
     * @param ctx
     * @param userId
     * @param appId
     * @param roomId
     */
    public void loginSuccessHandler(ChannelHandlerContext ctx, Long userId, int appId,Integer roomId) {
        // 为当前的channel绑定用户id，存入map集合
        ChannelHandlerContextCache.put(userId, ctx);
        // todo  attr()方法的功能是可以用于做一些二次开发，给当前的channel绑定一些属性
        //  在这里我们给我们的channel绑定我们当前用户的userId，后序也可以再从channel中拿到我们绑定过的userId
        // ctx.attr(ImContextAttr.USER_ID).set(userId);
        ImContextUtils.setUserId(ctx, userId);
        ImContextUtils.setAppId(ctx, appId);
        ImContextUtils.setRoomId(ctx,roomId);
        // 设置响应信息返回给请求登录的客户端
        ImMsgBody respBody=new ImMsgBody();
        respBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
        respBody.setUserId(userId);
        respBody.setData("true");
        ImMsg respMsg=ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
        // 用户登录成功后，绑定当前用户的id和与用户建立长连接的IM服务器的地址
        // 设置过期时间为心跳时间的2倍
        stringRedisTemplate.opsForValue().set(ImCoreServerConstants.IM_BIND_IP_KEY+ appId +":"+ userId,
                ChannelHandlerContextCache.getServerIpAddress()+"%"+userId,
                ImConstants.DEFAULT_HEART_BEAT_GAP*2, TimeUnit.SECONDS);
        LOGGER.info("[LoginMsgHandler] login success, userId is {}, appId is {}", userId, appId);
        // 放行，允许当前用户建立连接
        ctx.writeAndFlush(respMsg);
        // 发送MQ消息记录当前用户加入直播间的关联信息
        sendLoginMQ(userId,appId,roomId);
    }


    /***
     * 当用户登录成功后发送MQ消息实现向当前直播间的人员集合中加入当前用户id
     * @param userId
     * @param appId
     * @param roomId
     */
    private void sendLoginMQ(Long userId,Integer appId,Integer roomId) {
        // 设置要发送的数据
        ImOnlineDTO imOnlineDTO=new ImOnlineDTO();
        imOnlineDTO.setUserId(userId);
        imOnlineDTO.setAppId(appId);
        imOnlineDTO.setRoomId(roomId);
        imOnlineDTO.setLoginTime(System.currentTimeMillis());
        Message message=new Message();
        // 设置mq发送的主题为用户上线
        message.setTopic(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC);
        message.setBody(JSON.toJSONString(imOnlineDTO).getBytes());
        try {
            // 发送
            SendResult sendResult = mqProducer.send(message);
            LOGGER.info("[sendLoginMQ] sendResult is {}", sendResult);
        } catch (Exception e) {
            LOGGER.error("[sendLoginMQ] error is",e);
        }
    }

}
