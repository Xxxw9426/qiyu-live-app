package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 直播间相关操作Redis中key值生成器
 * @Version: 1.0
 */

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class LivingProviderCacheKeyBuilder extends RedisKeyBuilder{


    /** 存入Redis的当前正在直播的直播间列表的缓存的key值 */
    private static String LIVING_ROOM_LIST = "living_room_list";


    /** 存入Redis的当前直播间的相关信息的缓存的key值 */
    private static String LIVING_ROOM_OBJ = "living_room_obj";


    /** 在刷新正在直播的直播间列表时用到的Redis的锁的key值 */
    private static String REFRESH_LIVING_ROOM_LIST_LOCK = "refresh_living_room_list_lock";


    /** 存入Redis的当前直播间在线用户的set集合的key值 */
    private static String LIVING_ROOM_USER_SET = "living_room_user_set";


    /** 存入Redis的当前正在直播的直播间列表的缓存的key值 */
    public String buildLivingRoomList(Integer type) {
        return super.getPrefix() + LIVING_ROOM_LIST + super.getSplitItem() + type;
    }


    /** 存入Redis的当前直播间的相关信息的缓存的key值 */
    public String buildLivingRoomObj(Integer roomId) {
        return super.getPrefix() + LIVING_ROOM_OBJ + super.getSplitItem() + roomId;
    }


    /** 在刷新正在直播的直播间列表时用到的Redis的锁的key值 */
    public String buildRefreshLivingRoomListLock() {
        return super.getPrefix() + REFRESH_LIVING_ROOM_LIST_LOCK;
    }


    /** 存入Redis的当前直播间在线用户的set集合的key值 */
    public String buildLivingRoomUserSet(Integer roomId, Integer appId) {
        return super.getPrefix() + LIVING_ROOM_USER_SET + super.getSplitItem() + appId + super.getSplitItem() + roomId;
    }
}
