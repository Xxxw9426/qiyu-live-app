package org.qiyu.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import org.qiyu.live.bank.constants.TradeTypeEnum;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.dto.QiyuCurrencyAccountDTO;
import org.qiyu.live.bank.provider.dao.mapper.IQiyuCurrencyAccountMapper;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyAccountPO;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyAccountService;
import org.qiyu.live.bank.provider.service.IQiyuCurrencyTradeService;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 账户余额相关操作service接口实现类
 * @Version: 1.0
 */
@Service
public class QiyuCurrencyAccountServiceImpl implements IQiyuCurrencyAccountService {


    @Resource
    private IQiyuCurrencyAccountMapper qiyuCurrencyAccountMapper;


    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Resource
    private BankProviderCacheKeyBuilder bankProviderCacheKeyBuilder;


    @Resource
    private IQiyuCurrencyTradeService qiyuCurrencyTradeService;


    private static ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(2,4,30,TimeUnit.SECONDS,new ArrayBlockingQueue<>(1000));


    /***
     * 新增账户
     *  如果插入失败的话只返回false，不抛出错误
     * @param userId
     */
    @Override
    public boolean insertOne(long userId) {
        try {
            QiyuCurrencyAccountPO qiyuCurrencyAccountPO = new QiyuCurrencyAccountPO();
            qiyuCurrencyAccountPO.setUserId(userId);
            qiyuCurrencyAccountPO.setCreateTime(new Date());
            qiyuCurrencyAccountPO.setUpdateTime(new Date());
            qiyuCurrencyAccountMapper.insert(qiyuCurrencyAccountPO);
            return true;
        } catch (Exception e) {
        }
        return false;
    }


    /***
     * 增长余额
     * @param userId
     * @param num 增长的金额
     */
    @Override
    public void incr(long userId, int num) {
        // 首先对缓存进行处理
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        // 如果缓存没有过期的话先给缓存增加
        if(redisTemplate.hasKey(cacheKey)){
            redisTemplate.opsForValue().increment(cacheKey,num);
            redisTemplate.expire(cacheKey,5,TimeUnit.MINUTES);
        }
        // 异步的从DB中增加用户余额并且做流水记录
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 开启一个线程异步的对DB进行操作
                consumeIncrDBHandler(userId,num);
            }
        });
    }


    /***
     * 减少余额并且做流水记录
     * @param userId
     * @param num  减少的金额
     */
    @Override
    public void decr(long userId, int num) {
        // 先在Redis中做扣减，然后再在DB中进行异步更新
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        // 如果缓存没有过期的话先给缓存扣减
        if(redisTemplate.hasKey(cacheKey)){
            // 基于Redis的扣减操作
            redisTemplate.opsForValue().decrement(cacheKey,num);
            redisTemplate.expire(cacheKey,5,TimeUnit.MINUTES);
        }
        // 异步的从DB中扣减用户余额并且做流水记录
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 开启一个线程异步的对DB进行操作
                consumeDecrDBHandler(userId,num);
            }
        });
    }


    /***
     * 查询账户余额
     * @param userId
     * @return
     */
    @Override
    public Integer getBalance(long userId) {
        // 首先从缓存中获取当前用户账户的余额
        String cacheKey = bankProviderCacheKeyBuilder.buildUserBalance(userId);
        Object cacheBalance = redisTemplate.opsForValue().get(cacheKey);
        // 如果缓存命中
        if(cacheBalance != null) {
            // 如果是空值缓存
            if((Integer)cacheBalance==-1) {
                return null;
            }
            return (Integer) cacheBalance;
        }
        // 缓存未命中
        // 从数据库进行查询
        Integer currentBalance = qiyuCurrencyAccountMapper.queryBalance(userId);
        // 数据库中未查询到
        if(currentBalance == null){
            // 设置Redis的空值缓存
            redisTemplate.opsForValue().set(cacheKey,-1,5,TimeUnit.MINUTES);
            return null;
        }
        // 数据库中查询到了
        redisTemplate.opsForValue().set(cacheKey,currentBalance,30, TimeUnit.MINUTES);
        return currentBalance;
    }


    /***
     * 判断用户当前余额是否充足，充足的话进行扣减消费，不充足的话拦截
     * @param req
     * @return
     */
    @Override
    public AccountTradeRespDTO consume(AccountTradeReqDTO req) {
        /*long userId=req.getUserId();
        int num=req.getNum();
        // 首先判断当前用户的账户余额是否充足
        Integer balance = this.getBalance(userId);
        // 账户未初始化
        if(balance==null ){
            return AccountTradeRespDTO.buildFail(userId,"账户没有初始化",1);
        }
        // 如果账户余额不足
        if(balance-num<0) {
            return AccountTradeRespDTO.buildFail(userId,"账户余额不足",3);
        }
        // 扣减余额
        this.decr(userId,num);
        return AccountTradeRespDTO.buildSuccess(userId,"扣费成功！");*/
        return null;
    }


    /***
     * 专门给送礼业务调用的扣减余额的逻辑方法
     * @param req
     * @return
     */
    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO req) {
        // 余额判断
        long userId=req.getUserId();
        int num=req.getNum();
        Integer balance = this.getBalance(userId);
        if(balance==null || balance<num){
            return AccountTradeRespDTO.buildFail(userId,"账户余额不足",1);
        }
        // 扣减余额并且记录流水
        this.decr(userId,num);
        return AccountTradeRespDTO.buildSuccess(userId,"消费成功");
    }


    /***
     * 专门用于异步的对DB进行操作
     * 对DB中的用户余额进行增加
     * 添加流水记录
     * @param userId
     * @param num
     */
    @Transactional(rollbackFor = Exception.class)
    public void consumeIncrDBHandler(long userId,int num) {
        qiyuCurrencyAccountMapper.incr(userId, num);
        qiyuCurrencyTradeService.insertOne(userId,num, TradeTypeEnum.SEND_GIFT_TRADE.getCode());
    }


    /***
     * 专门用于异步的对DB进行操作
     * 对DB中的用户余额进行扣减
     * 添加流水记录
     * @param userId
     * @param num
     */
    @Transactional(rollbackFor = Exception.class)
    public void consumeDecrDBHandler(long userId,int num) {
        qiyuCurrencyAccountMapper.decr(userId,num);
        qiyuCurrencyTradeService.insertOne(userId,num*-1, TradeTypeEnum.SEND_GIFT_TRADE.getCode());
    }
}
