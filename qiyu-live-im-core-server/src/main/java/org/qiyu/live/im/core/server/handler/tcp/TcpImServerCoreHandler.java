package org.qiyu.live.im.core.server.handler.tcp;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.ImHandlerFactory;
import org.qiyu.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: TCP连接中接收外界所有消息的handler处理器
 * @Version: 1.0
 */

@Component
@ChannelHandler.Sharable
public class TcpImServerCoreHandler extends SimpleChannelInboundHandler {


    @Resource
    private ImHandlerFactory imHandlerFactory;


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;


    // todo 我们的消息在经过了解码器的处理后会进入channelRead0()方法
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 如果传入的消息类型不是ImMsg类型的消息则说明参数异常
        if(!(msg instanceof ImMsg)) {
            throw new IllegalArgumentException("error msg,msg is :" + msg);
        }
        ImMsg imMsg = (ImMsg) msg;
        // 调用handler工厂类处理传入的消息
        imHandlerFactory.doMsgHandler(ctx, imMsg);
    }


    // todo netty提供的监听不活跃channel(正常或意外断线)的方法
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 在这个方法中，首先从当前的channel中拿到我们之前给它绑定的userId，然后从我们设置的channel的缓存map中移除
        // Long userId=ctx.attr(ImContextAttr.USER_ID).get();
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId != null && appId != null) {
            // 客户端异常掉线，移除掉ChannelHandlerContextCache中记录的当前用户绑定的channel
            ChannelHandlerContextCache.remove(userId);
            // 删除供Router取出的存在Redis的IM服务器的ip+端口地址
            stringRedisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
            // 删除心跳包存活缓存
            stringRedisTemplate.delete(cacheKeyBuilder.buildOnlineZsetKey(userId, appId));
        }
    }
}
