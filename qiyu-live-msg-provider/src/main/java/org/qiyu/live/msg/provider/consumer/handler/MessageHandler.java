package org.qiyu.live.msg.provider.consumer.handler;

import org.qiyu.live.im.dto.ImMsgBody;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-29
 * @Description: 处理IM服务中BizHandler放入消息队列中的消息的handler接口
 * @Version: 1.0
 */

public interface MessageHandler {


    /***
     * 处理IM服务中BizHandler放入消息队列中的消息
     * @param imMsgBody
     */
    void onMsgReceive(ImMsgBody imMsgBody);
}
