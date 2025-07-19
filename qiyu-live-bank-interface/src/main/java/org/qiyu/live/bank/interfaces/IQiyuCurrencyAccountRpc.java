package org.qiyu.live.bank.interfaces;

import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 账户余额相关操作rpc接口
 * @Version: 1.0
 */

public interface IQiyuCurrencyAccountRpc {


    /***
     * 增长余额
     * @param userId
     * @param num 增长的金额
     */
    void incr(long userId,int num);


    /***
     * 减少余额
     * @param userId
     * @param num  减少的金额
     */
    void decr(long userId,int num);


    /***
     * 减少余额方法版本2
     * @param userId
     * @param num
     * @return
     */
    boolean decrV2(long userId,int num);


    /***
     * 查询账户余额
     * @param userId
     * @return
     */
    Integer getBalance(long userId);


    /***
     * 专门给送礼业务调用的扣减余额的逻辑方法
     * @param req
     * @return
     */
    AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO req);

}
