package org.qiyu.live.im.provider.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.qiyu.live.im.provider.service.ImOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-01
 * @Description: 校验用户是否在线的service接口实现类
 * @Version: 1.0
 */
@Service
public class ImOnlineServiceImpl implements ImOnlineService {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /***
     * 判断传入的userId的用户是否与我们的Im服务建立了连接，即是否在线
     * @param userId
     * @param appId
     * @return
     */
    @Override
    public boolean isOnline(long userId, int appId) {
        // 直接根据Redis中的缓存判断传入的userId对应的用户是否在线
        return redisTemplate.hasKey(ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId);
    }
}
