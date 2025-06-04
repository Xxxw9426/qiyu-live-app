package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Conditional;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: 其他
 * @Version: 1.0
 */

@Configurable
@Conditional(RedisKeyLoadMatch.class)
public class OtherCacheKeyBuilder extends RedisKeyBuilder {

    private static String USER_INFO_KEY = "other";

    public String buildUserInfoKey(Long userId) {
        return super.getPrefix() + USER_INFO_KEY +
                super.getSplitItem() + userId;
    }
}