package org.qiyu.live.im.interfaces;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: Im服务中的token服务rpc接口
 * @Version: 1.0
 */

public interface ImTokenRpc {


    /***
     *  创建用户登录Im服务的token
     * @param userId
     * @param appId
     * @return
     */
    String createImLoginToken(long userId,int appId);


    /***
     *  根据token检索用户id
     * @param token
     * @return
     */
    Long getUserIdByToken(String token);
}
