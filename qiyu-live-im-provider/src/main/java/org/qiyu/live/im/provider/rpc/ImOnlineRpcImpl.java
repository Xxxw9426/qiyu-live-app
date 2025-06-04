package org.qiyu.live.im.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.interfaces.ImOnlineRpc;
import org.qiyu.live.im.provider.service.ImOnlineService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-01
 * @Description: 校验用户是否在线的rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class ImOnlineRpcImpl implements ImOnlineRpc {


    @Resource
    private ImOnlineService imOnlineService;


    /***
     * 判断传入的userId的用户是否与我们的Im服务建立了连接，即是否在线
     * @param userId
     * @param appId
     * @return
     */
    @Override
    public boolean isOnline(long userId, int appId) {
        return imOnlineService.isOnline(userId, appId);
    }
}
