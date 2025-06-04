package org.qiyu.live.im.core.server.handler.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import jakarta.annotation.Resource;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.ImHandlerFactory;
import org.qiyu.live.im.core.server.handler.impl.LogoutMsgHandler;
import org.qiyu.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: WebSocket连接中接收外界所有消息的handler处理器
 * @Version: 1.0
 */

@Component
@ChannelHandler.Sharable
public class WsImServerCoreHandler extends SimpleChannelInboundHandler {


    private static final Logger LOGGER = LoggerFactory.getLogger(WsImServerCoreHandler.class);


    @Resource
    private ImHandlerFactory imHandlerFactory;


    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    // todo 我们的消息在经过了解码器的处理后会进入channelRead0()方法
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            // 传入处理消息的方法
            wsMsgHandler(ctx, (WebSocketFrame) msg);
        }
    }


    /**
     * 客户端正常或意外掉线，都会触发这里
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId !=null && appId != null) {
            ChannelHandlerContextCache.remove(userId);
            // 移除用户之前连接上的ip记录
            redisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId);
        }
    }


    /***
     * 处理传过来的WebSocket消息
     * @param ctx
     * @param msg
     */
    private void wsMsgHandler(ChannelHandlerContext ctx, WebSocketFrame msg) {
        // 如过不是文本消息，统一后台不会处理
        if (!(msg instanceof TextWebSocketFrame)) {
            LOGGER.error("[WsImServerCoreHandler] wsMsgHandler, {} msg types not supported", msg.getClass().getName());
            return;
        }
        try {
            // 返回应答消息
            String content = (((TextWebSocketFrame) msg).text());
            JSONObject jsonObject = JSON.parseObject(content, JSONObject.class);
            ImMsg imMsg = new ImMsg();
            imMsg.setMagic(jsonObject.getShort("magic"));
            imMsg.setCode(jsonObject.getInteger("code"));
            imMsg.setLen(jsonObject.getInteger("len"));
            imMsg.setBody(jsonObject.getString("body").getBytes());
            imHandlerFactory.doMsgHandler(ctx, imMsg);
        } catch (Exception e) {
            LOGGER.error("[WsImServerCoreHandler] wsMsgHandler error is ", e);
        }
    }
}
