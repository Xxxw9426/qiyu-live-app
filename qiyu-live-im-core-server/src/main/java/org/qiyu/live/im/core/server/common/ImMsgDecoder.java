package org.qiyu.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.qiyu.live.im.constants.ImConstants;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: 使用netty接收消息的解码器
 * // todo 这个类中主要处理的是将从外界发送给我们服务器的消息解码为ImMsg对象
 * @Version: 1.0
 */

public class ImMsgDecoder extends ByteToMessageDecoder {

    //ImMsg的最低基本字节数
    private final int BASE_LEN = 2 + 4 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        // 首先对ByteBuf数组的内容进行基本校验:长度校验，magic校验
        // 长度大于ImMsg的最小长度
        if(byteBuf.readableBytes() >= BASE_LEN) {
            // 检验magic的值
            // magic值不符合要求
            if(byteBuf.readShort()!= ImConstants.DEFAULT_MAGIC) {
                ctx.close();
                return;
            }
            // 都符合要求，读取byteBuf中的信息
            int code=byteBuf.readInt();
            int len=byteBuf.readInt();
            // 再次判断剩余可读消息体长度是否大于我们传过来的长度len
            if(byteBuf.readableBytes() < len) {
                ctx.close();
                return;
            }
            byte[] body=new byte[len];
            byteBuf.readBytes(body);
            // 将读取到的信息存入ImMsg对象
            ImMsg imMsg=new ImMsg();
            imMsg.setCode(code);
            imMsg.setLen(len);
            imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
            imMsg.setBody(body);
            out.add(imMsg);
        }
    }
}
