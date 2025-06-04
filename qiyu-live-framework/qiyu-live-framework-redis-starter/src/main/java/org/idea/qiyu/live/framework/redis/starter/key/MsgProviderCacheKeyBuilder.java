package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-12
 * @Description: 短信验证模块redis中key值的生成器
 * @Version: 1.0
 */

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class MsgProviderCacheKeyBuilder extends RedisKeyBuilder{


    /**  存入Redis的短信验证码的key值  */
    private static String SMS_LOGIN_CODE_KEY = "smsLoginCode";


    /***
     * 存入Redis的短信验证码的key值
     * @param phone
     * @return
     */
    public String buildSmsLoginCodeKey(String phone) {
        return super.getPrefix()+SMS_LOGIN_CODE_KEY+super.getSplitItem()+phone;
    }

}
