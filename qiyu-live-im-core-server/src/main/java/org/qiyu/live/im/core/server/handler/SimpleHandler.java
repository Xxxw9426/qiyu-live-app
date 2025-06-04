package org.qiyu.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.qiyu.live.im.core.server.common.ImMsg;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: 整合所有handler的一个接口工具，我们项目中设计的所有handler都继承自这个接口
 * @Version: 1.0
 */

public interface SimpleHandler {

    /***
     * 消息处理函
     * @param ctx
     * @param msg
     */
    void handler(ChannelHandlerContext ctx, ImMsg msg);
}
