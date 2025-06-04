package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: Im-Provider模块redis中key值的生成器
 * @Version: 1.0
 */

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ImProviderCacheKeyBuilder extends RedisKeyBuilder{


    /** 存入Redis的IM服务中当前登录用户的token值的key值 */
    private static String IM_LOGIN_TOKEN = "imLoginToken";


    /***
     * 存入Redis的IM服务中当前登录用户的token值的key值
     * @param token
     * @return
     */
    public String buildImLoginTokenKey(String token) {
        return super.getPrefix() + IM_LOGIN_TOKEN + super.getSplitItem() + token;
    }
}
