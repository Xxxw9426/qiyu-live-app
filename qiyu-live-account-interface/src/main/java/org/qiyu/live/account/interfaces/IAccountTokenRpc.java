package org.qiyu.live.account.interfaces;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-15
 * @Description: token服务rpc接口
 * @Version: 1.0
 */

public interface IAccountTokenRpc {


    /***
     * 创建一个登录token并且将其保存到Redis中
     * @param userId
     * @return
     */
    String createAndSaveLoginToken(Long userId);


    /***
     * 从Redis中拿到token校验用户token
     * @param tokenKey
     * @return
     */
    Long getUserIdByToken(String tokenKey);
}
