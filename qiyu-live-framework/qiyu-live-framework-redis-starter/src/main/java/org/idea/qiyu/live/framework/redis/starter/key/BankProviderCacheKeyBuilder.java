package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 钱包模块redis中key值的生成器
 * @Version: 1.0
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class BankProviderCacheKeyBuilder extends RedisKeyBuilder{


    /** 存入Redis的当前用户账户余额的key值 */
    private static String BALANCE_CACHE = "balance_cache";


    /** 存入Redis的对应type的付费产品的缓存的key值 */
    private static String PAY_PRODUCT_CACHE = "pay_product_cache";


    /** 存入Redis的具体id的付费产品的缓存的key值 */
    private static String PAY_PRODUCT_ITEM_CACHE = "pay_product_item_cache";


    /** 存入Redis的当前用户账户余额的key值 */
    public String buildUserBalance(Long userId) {
        return super.getPrefix() + BALANCE_CACHE + super.getSplitItem() + userId;
    }


    /** 存入Redis的对应type的付费产品的缓存的key值 */
    public String buildPayProductCache(Integer type) {
        return super.getPrefix() + PAY_PRODUCT_CACHE + super.getSplitItem() + type;
    }


    /** 存入Redis的具体id的付费产品的缓存的key值 */
    public String buildPayProductItemCache(Integer productId) {
        return super.getPrefix() + PAY_PRODUCT_ITEM_CACHE + super.getSplitItem() + productId;
    }
}
