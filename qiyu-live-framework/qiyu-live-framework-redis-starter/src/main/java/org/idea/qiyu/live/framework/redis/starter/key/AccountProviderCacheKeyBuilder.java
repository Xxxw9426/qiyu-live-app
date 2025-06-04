package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-15
 * @Description: token服务中与redis有关的操作的key值生成类
 * @Version: 1.0
 */

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class AccountProviderCacheKeyBuilder extends RedisKeyBuilder{


    /** 存入Redis的当前登录用户的token值的key值  */
    private static String ACCOUNT_TOKEN_KEY ="account";


    /***
     * 存入Redis的当前登录用户的token值的key值
     * @param key
     * @return
     */
    public String buildUserLoginTokenKey(String key) {
        return super.getPrefix()+ACCOUNT_TOKEN_KEY+super.getSplitItem()+key;
    }
}
