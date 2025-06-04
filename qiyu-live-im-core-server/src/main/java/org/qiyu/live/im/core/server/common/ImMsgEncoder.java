package org.qiyu.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: 使用netty发送消息的编码器
 * // todo 这个类中主要处理的是对从服务端发送出去的消息ImMsg类进行一次封装
 * @Version: 1.0
 */

public class ImMsgEncoder extends MessageToByteEncoder {


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        ImMsg imMsg = (ImMsg) msg;
        out.writeShort(imMsg.getMagic());
        out.writeInt(imMsg.getCode());
        out.writeInt(imMsg.getLen());
        out.writeBytes(imMsg.getBody());
        // ctx.writeAndFlush(out);
    }
}
