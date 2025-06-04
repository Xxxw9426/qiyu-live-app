package org.qiyu.live.im.core.server.service;

import org.qiyu.live.im.dto.ImMsgBody;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-29
 * @Description: Im服务对外暴露的专门给router层服务进行调用的rpc接口逻辑实现service接口
 * @Version: 1.0
 */

public interface IRouterHandlerService {


    /***
     * 根据消息接收者所在机器地址向消息接收者发送消息
     * @param imMsgBody
     */
    void onReceive(ImMsgBody imMsgBody);


    /***
     * 发送消息给客户端
     * @param imMsgBody
     */
    boolean sendMsgToClient(ImMsgBody imMsgBody);
}
