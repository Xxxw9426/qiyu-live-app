package org.qiyu.live.im.provider.service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-01
 * @Description: 校验用户是否在线的service接口
 * @Version: 1.0
 */

public interface ImOnlineService {


    /***
     * 判断传入的userId的用户是否与我们的Im服务建立了连接，即是否在线
     * @param userId
     * @param appId
     * @return
     */
    boolean isOnline(long userId,int appId);
}
