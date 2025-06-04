package org.qiyu.live.im.router.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.qiyu.live.im.router.provider.service.ImRouterService;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: 专门做请求转发的router层rpc接口实现类
 * @Version: 1.0
 */

@DubboService
public class ImRouterRpcImpl implements ImRouterRpc {


    @Resource
    private ImRouterService imRouterService;


    /***
     * 根据用户id找到用户所在的服务器地址并转发消息
     * @param imMsgBody
     * @return
     */
    @Override
    public boolean sendMsg(ImMsgBody imMsgBody) {
        return imRouterService.sendMsg(imMsgBody);
    }


    /***
     * 批量操作：根据用户id找到用户所在的服务器地址并转发消息
     * @param imMsgBodyList
     */
    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        imRouterService.batchSendMsg(imMsgBodyList);
    }
}
