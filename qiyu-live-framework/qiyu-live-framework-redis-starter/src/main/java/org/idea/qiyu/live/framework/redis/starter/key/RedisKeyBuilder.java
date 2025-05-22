package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Value;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-01
 * @Description: 存一些Redis的key
 * @Version: 1.0
 */

public class RedisKeyBuilder {

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String SPLIT_ITEM = ":";

    public String getSplitItem() {
        return SPLIT_ITEM;
    }

    public String getPrefix() {
        return applicationName + SPLIT_ITEM;
    }
}
