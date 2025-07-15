package org.qiyu.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyAccountService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 账户余额相关操作rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class QiyuCurrencyAccountRpcImpl implements IQiyuCurrencyAccountRpc {


    @Resource
    private IQiyuCurrencyAccountService qiyuCurrencyAccountService;


    /***
     * 增长余额
     * @param userId
     * @param num 增长的金额
     */
    @Override
    public void incr(long userId, int num) {
        qiyuCurrencyAccountService.incr(userId, num);
    }


    /***
     * 减少余额
     * @param userId
     * @param num  减少的金额
     */
    @Override
    public void decr(long userId, int num) {
        qiyuCurrencyAccountService.decr(userId, num);
    }


    /***
     * 查询账户余额
     * @param userId
     * @return
     */
    @Override
    public Integer getBalance(long userId) {
        return qiyuCurrencyAccountService.getBalance(userId);
    }


    /***
     * 专门给送礼业务调用的扣减余额的逻辑方法
     * @param req
     * @return
     */
    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO req) {
        return qiyuCurrencyAccountService.consumeForSendGift(req);
    }


}
