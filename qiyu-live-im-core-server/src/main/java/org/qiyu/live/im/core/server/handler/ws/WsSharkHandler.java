package org.qiyu.live.im.core.server.handler.ws;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.im.core.server.handler.impl.LoginMsgHandler;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: WebSocket协议的握手连接处理器:在初次进行WebSocket连接的时候握手的定义
 * @Version: 1.0
 */

@Component
@ChannelHandler.Sharable
@RefreshScope
public class WsSharkHandler extends ChannelInboundHandlerAdapter {


    private static final Logger LOGGER = LoggerFactory.getLogger(WsSharkHandler.class);


    @Value("${qiyu.im.ws.port}")
    private int port;


    @Value("${spring.cloud.nacos.discovery.ip}")
    private String serverIp;


    @DubboReference
    private ImTokenRpc imTokenRpc;


    @Resource
    private LoginMsgHandler loginMsgHandler;


    private WebSocketServerHandshaker webSocketServerHandshaker;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 握手连接接入ws
        if (msg instanceof FullHttpRequest) {
            handlerHttpRequest(ctx, (FullHttpRequest) msg);
            return;
        }
        // 正常关闭链路
        if (msg instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) ((WebSocketFrame) msg).retain());
            return;
        }
        ctx.fireChannelRead(msg);
    }


    /***
     * 握手的处理
     * @param ctx
     * @param msg
     */
    private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        // 前端发送的连接ws url格式：ws://127.0.0.1:8809/{token}/{userId}/{code}/{param}
        String webSocketUrl = "ws://" + serverIp + ":" + port;
        // 构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketUrl, null, false);
        // 建立webSocket连接之前进行参数校验
        // 获取前端传过来的参数
        String uri = msg.uri();
        String[] paramArr = uri.split("/");
        String token = paramArr[1];
        Long userId = Long.valueOf(paramArr[2]);
        Integer roomId=null;
        // 通过rpc调用校验token
        Long queryUserId = imTokenRpc.getUserIdByToken(token);
        // token的最后与%拼接的就是appId
        Integer appId = Integer.valueOf(token.substring(token.indexOf("%") + 1));
        System.out.println(token);
        System.out.println(queryUserId);
        System.out.println(userId);
        // token校验不通过关闭连接，返回错误信息
        if (queryUserId == null || !queryUserId.equals(userId)) {
            LOGGER.error("[WsSharkHandler] token 校验不通过！");
            ctx.close();
            return;
        }
        // 参数校验通过，建立ws握手连接
        webSocketServerHandshaker = wsFactory.newHandshaker(msg);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }
        // 真正建立连接
        ChannelFuture channelFuture = webSocketServerHandshaker.handshake(ctx.channel(), msg);
        // 首次握手建立ws连接后，返回一定的内容给到客户端
        if (channelFuture.isSuccess()) {
            loginMsgHandler.loginSuccessHandler(ctx,userId,appId,roomId);
            LOGGER.info("[WebsocketSharkHandler] channel is connect!");
        }
    }

    enum ParamCodeEnum {
        LIVING_ROOM_LOGIN(1001, "直播间登录");
        int code;
        String desc;
        ParamCodeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        public int getCode() {
            return code;
        }
        public String getDesc() {
            return desc;
        }
    }
}

