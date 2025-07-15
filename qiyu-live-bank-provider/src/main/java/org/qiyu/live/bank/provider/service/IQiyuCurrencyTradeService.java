package org.qiyu.live.bank.provider.service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 流水记录相关操作service接口
 * @Version: 1.0
 */

public interface IQiyuCurrencyTradeService {


    /***
     * 插入一条流水记录
     * @param userId
     * @param num
     * @param type
     * @return
     */
    boolean insertOne(long userId,int num,int type);
}
