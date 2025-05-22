package org.qiyu.live.user.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-07
 * @Description: 用户服务中rocketMQ监听的topic常量
 * @Version: 1.0
 */

public class UserProviderTopicNames {

    /**
     * 专门处理和用户信息相关的缓存延迟删除操作时rocketMQ监听的topic常量
     */
    public static final String CACHE_ASYNC_DELETE_TOPIC = "UserCacheAsyncDelete";

}
