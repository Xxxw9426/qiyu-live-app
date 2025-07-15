package org.qiyu.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.bank.provider.dao.mapper.IQiyuCurrencyTradeMapper;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyTradePO;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 流水记录相关操作service接口实现类
 * @Version: 1.0
 */
@Service
public class QiyuCurrencyTradeServiceImpl implements IQiyuCurrencyTradeService {


    private static final Logger LOGGER= LoggerFactory.getLogger(QiyuCurrencyTradeServiceImpl.class);


    @Resource
    private IQiyuCurrencyTradeMapper qiyuCurrencyTradeMapper;


    /***
     * 插入一条流水记录
     * @param userId
     * @param num
     * @param type
     * @return
     */
    @Override
    public boolean insertOne(long userId, int num, int type) {
        try {
            QiyuCurrencyTradePO qiyuCurrencyTradePO = new QiyuCurrencyTradePO();
            qiyuCurrencyTradePO.setUserId(userId);
            qiyuCurrencyTradePO.setNum(num);
            qiyuCurrencyTradePO.setType(type);
            qiyuCurrencyTradeMapper.insert(qiyuCurrencyTradePO);
            return true;
        } catch (Exception e) {
            LOGGER.error("[QiyuCurrencyTradeServiceImpl] insert error, error is:", e);
        }
        return false;
    }
}
