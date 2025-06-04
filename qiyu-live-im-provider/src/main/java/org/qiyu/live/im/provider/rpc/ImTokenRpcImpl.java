package org.qiyu.live.im.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.qiyu.live.im.provider.service.ImTokenService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: Im服务中的token服务rpc接口实现类
 * @Version: 1.0
 */

@DubboService
public class ImTokenRpcImpl implements ImTokenRpc {


    @Resource
    private ImTokenService imTokenService;


    /***
     *  创建用户登录Im服务的token
     * @param userId
     * @param appId
     * @return
     */
    @Override
    public String createImLoginToken(long userId, int appId) {
        return imTokenService.createImLoginToken(userId, appId);
    }


    /***
     *  根据token检索用户id
     * @param token
     * @return
     */
    @Override
    public Long getUserIdByToken(String token) {
        return imTokenService.getUserIdByToken(token);
    }
}
