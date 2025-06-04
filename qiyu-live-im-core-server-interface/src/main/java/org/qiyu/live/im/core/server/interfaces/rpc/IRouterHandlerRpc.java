package org.qiyu.live.im.core.server.interfaces.rpc;

import org.qiyu.live.im.dto.ImMsgBody;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: Im服务对外暴露的,专门给router层服务进行调用的rpc接口
 * @Version: 1.0
 */

public interface IRouterHandlerRpc {


    /***
     * 根据用户id找到用户所在的服务器地址并转发消息
     * @param imMsgBody
     */
    void sendMsg(ImMsgBody imMsgBody);


    /***
     * 批量操作：根据用户id找到用户所在的服务器地址并转发消息
     * @param imMsgBodyList
     */
    void batchSendMsg(List<ImMsgBody> imMsgBodyList);
}
