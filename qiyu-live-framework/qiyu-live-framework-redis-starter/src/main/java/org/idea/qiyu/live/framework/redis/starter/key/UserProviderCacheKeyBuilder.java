package org.idea.qiyu.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-01
 * @Description: 用户中台服务中的redis的key值
 * @Version: 1.0
 */
@Configuration
// 这个注解的作用是当我们在项目中注入这个类的时候，首先会去判断是否满足括号中这个类的返回值
@Conditional(RedisKeyLoadMatch.class)
public class UserProviderCacheKeyBuilder extends RedisKeyBuilder{


    /** 存入Redis的用户信息的key值 */
    private static String USER_INFO_KEY = "userInfo";

    /** 存入Redis的用户标签操作锁的key值 */
    private static String USER_TAG_LOCK_KEY="userTagLock";

     /** 存入Redis的用户标签数据对象缓存的key值 */
    private static String USER_TAG_KEY="userTag";

     /** 存入Redis的用户登录或注册时生成的token的key值 */
    private static String USER_LOGIN_TOKEN_KEY = "userLoginToken";

     /** 存入Redis的用户电话对应的用户信息的key值 */
    private static String USER_PHONE_OBJ_KEY = "userPhoneObj";

     /** 存入redis的当前userId对应的所有电话关联用户信息的key值 */
    private static String USER_PHONE_LIST_KEY = "userPhoneList";


    /**
     * 存入Redis的用户信息的key值
     * @param userId
     * @return
     */
    public String buildUserInfoKey(Long userId) {
        return super.getPrefix() + USER_INFO_KEY +
                super.getSplitItem() + userId;
    }


    /***
     *  存入Redis的用户标签操作锁的key值
     * @param userId
     * @return
     */
    public String buildTagLockKey(Long userId) {
        return super.getPrefix()+ USER_TAG_LOCK_KEY + super.getSplitItem() + userId;
    }


    /***
     * 存入Redis的用户标签数据对象缓存的key值
     * @param userId
     * @return
     */
    public String buildTagKey(Long userId) {
        return super.getPrefix() + USER_TAG_KEY + super.getSplitItem() + userId;
    }


    /***
     * 存入Redis的用户登录或注册时生成的token的key值
     * @param tokenKey
     * @return
     */
    public String buildUserLoginTokenKey(String tokenKey) {
        return super.getPrefix() + USER_LOGIN_TOKEN_KEY + super.getSplitItem() + tokenKey;
    }


    /***
     * 存入Redis的用户电话对应的用户信息的key值
     * @param phone
     * @return
     */
    public String buildUserPhoneObjKey(String phone) {
        return super.getPrefix() + USER_PHONE_OBJ_KEY + super.getSplitItem() + phone;
    }


    /***
     * 存入redis的当前userId对应的所有电话关联用户信息的key值
     * @param userId
     * @return
     */
    public String buildUserPhoneListKey(Long userId) {
        return super.getPrefix() + USER_PHONE_LIST_KEY + super.getSplitItem() + userId;
    }


}
