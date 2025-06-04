package org.qiyu.live.im.core.server.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.qiyu.live.im.core.server.service.IRouterHandlerService;
import org.qiyu.live.im.dto.ImMsgBody;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: Im服务对外暴露的，专门给Router层服务进行调用的rpc接口实现类
 * @Version: 1.0
 */

@DubboService
public class RouterHandlerRpcImpl implements IRouterHandlerRpc {


    @Resource
    private IRouterHandlerService routerHandlerService;


    /***
     * 根据用户id找到用户所在的服务器地址并转发消息
     * @param imMsgBody
     */
    @Override
    public void sendMsg(ImMsgBody imMsgBody) {
        routerHandlerService.onReceive(imMsgBody);
    }


    /***
     * 批量操作：根据用户id找到用户所在的服务器地址并转发消息
     * @param imMsgBodyList
     */
    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        // 遍历传过来的消息体集合并且发送
        imMsgBodyList.forEach(imMsgBody -> {
            routerHandlerService.onReceive(imMsgBody);
        });
    }
}
