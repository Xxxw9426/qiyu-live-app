package org.qiyu.live.account.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.account.interfaces.IAccountTokenRpc;
import org.qiyu.live.account.provider.service.IAccountTokenService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-15
 * @Description: token服务rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class AccountTokenRpcImpl implements IAccountTokenRpc {


    @Resource
    private IAccountTokenService accountTokenService;


    /***
     * 创建一个登录token
     * @param userId
     * @return
     */
    @Override
    public String createAndSaveLoginToken(Long userId) {
        return accountTokenService.createAndSaveLoginToken(userId);
    }


    /***
     * 校验用户token
     * @param tokenKey
     * @return
     */
    @Override
    public Long getUserIdByToken(String tokenKey) {
        return accountTokenService.getUserIdByToken(tokenKey);
    }
}
