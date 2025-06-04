package org.qiyu.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: handler工厂类：根据传入消息的类型给出不同的handler做消息的处理和分发
 * @Version: 1.0
 */

public interface ImHandlerFactory {

    /***
     * 根据传入消息的类型给出不同的handler做消息的处理和分发
     * @param ctx
     * @param msg
     */
    void doMsgHandler(ChannelHandlerContext ctx, ImMsg msg);
}
