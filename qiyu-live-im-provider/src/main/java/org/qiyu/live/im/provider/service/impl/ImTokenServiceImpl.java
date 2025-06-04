package org.qiyu.live.im.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ImProviderCacheKeyBuilder;
import org.qiyu.live.im.provider.service.ImTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: Im服务中token服务service接口实现类
 * @Version: 1.0
 */
@Service
public class ImTokenServiceImpl implements ImTokenService {


    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Resource
    private ImProviderCacheKeyBuilder imProviderCacheKeyBuilder;


    /***
     * 创建用户登录Im服务的token
     * @param userId
     * @param appId
     * @return
     */
    @Override
    public String createImLoginToken(long userId, int appId) {
        // 使用UUID生成token
        String token = UUID.randomUUID() + "%" + appId;
        // 将生成的token存入Redis
        redisTemplate.opsForValue().set(imProviderCacheKeyBuilder.buildImLoginTokenKey(token), userId, 5L, TimeUnit.MINUTES);
        return token;
    }


    /***
     * 根据token检索用户id
     * @param token
     * @return
     */
    @Override
    public Long getUserIdByToken(String token) {
        Object userId = redisTemplate.opsForValue().get(imProviderCacheKeyBuilder.buildImLoginTokenKey(token));
        return userId == null ? null : Long.valueOf((Integer) userId);
    }
}
