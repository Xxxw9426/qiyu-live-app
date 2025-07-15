package org.qiyu.live.bank.api.service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付回调请求相关处理service
 * @Version: 1.0
 */

public interface IPayNotifyService {


    /***
     * 处理支付回调请求的逻辑实现
     * @param paramJson
     * @return
     */
    String notifyHandler(String paramJson);
}
