package org.qiyu.live.im.router.provider.service;

import org.qiyu.live.im.dto.ImMsgBody;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: 专门做请求转发的router层的service接口
 * @Version: 1.0
 */

public interface ImRouterService {


    /***
     * 根据用户id找到用户所在的服务器地址并转发消息
     * @param imMsgBody
     * @return
     */
    boolean sendMsg(ImMsgBody imMsgBody);


    /***
     * 批量操作：根据用户id找到用户所在的服务器地址并转发消息
     * @param imMsgBodyList
     */
    void batchSendMsg(List<ImMsgBody> imMsgBodyList);
}
