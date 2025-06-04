package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: ImCoreServer-Provider模块redis中key值的生成器
 * @Version: 1.0
 */

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ImCoreServerProviderCacheKeyBuilder extends RedisKeyBuilder{


    /** 存入Redis的IM服务中当前用户心跳消息的记录的key值 */
    private static String IM_ONLINE_ZSET = "imOnlineZset";


    /** 存入Redis的IM服务中的ACK消息记录的key值 */
    private static String IM_ACK_MAP = "imAckMap";


    /***
     * 存入Redis的IM服务中当前用户心跳消息的记录的key值 + appId + 当前用户Id % 10000
     * @param userId
     * @return
     */
    public String buildOnlineZsetKey(Long userId,Integer appId) {
        return super.getPrefix() + IM_ONLINE_ZSET + super.getSplitItem()  + appId + super.getSplitItem() + userId % 10000 ;
    }


    /***
     * 存入Redis的IM服务中的ACK消息记录的key值 + appId + 当前用户Id % 100
     * @param userId
     * @param appId
     * @return
     */
    public String buildImAckMapKey(Long userId,Integer appId) {
        return super.getPrefix() + IM_ACK_MAP + super.getSplitItem()  + appId + super.getSplitItem() + userId % 100 ;
    }
}
