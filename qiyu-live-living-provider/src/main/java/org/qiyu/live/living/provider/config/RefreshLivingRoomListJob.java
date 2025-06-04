package org.qiyu.live.living.provider.config;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.qiyu.live.living.interfaces.constants.LivingRoomTypeEnum;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 用于定期刷新Redis中缓存的直播间列表的list集合
 * @Version: 1.0
 */
@Configuration
public class RefreshLivingRoomListJob implements InitializingBean {


    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshLivingRoomListJob.class);


    @Resource
    private ILivingRoomService livingRoomService;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;


    // 延迟线程池，每隔1秒就会执行一次提交给该线程池的任务
    private static final ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1);


    @Override
    public void afterPropertiesSet() throws Exception {
        //一秒钟刷新一次直播间列表数据
        SCHEDULED_THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(new RefreshCacheListJob(), 3000, 1000, TimeUnit.MILLISECONDS);
    }


    // 由于我们是多线程高并发的场景，因此我们需要通过加锁来确保同一时间只会有一个线程拿到锁执行更新任务
    class RefreshCacheListJob implements Runnable{
        @Override
        public void run() {
            String cacheKey = cacheKeyBuilder.buildRefreshLivingRoomListLock();
            // 加锁
            Boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKey, 1, 1L, TimeUnit.SECONDS);
            // 如果拿到了锁，从数据库中读取正在直播的直播间列表写入Redis
            if (lockStatus) {
                LOGGER.info("[RefreshLivingRoomListJob] starting  更新数据库中记录的直播间到Redis中去");
                refreshDBToRedis(LivingRoomTypeEnum.DEFAULT_LIVING_ROOM.getCode());
                refreshDBToRedis(LivingRoomTypeEnum.PK_LIVING_ROOM.getCode());
                LOGGER.info("[RefreshLivingRoomListJob] end  更新数据库中记录的直播间到Redis中去");
            }
        }
    }


    /***
     * 从数据库中加载正在直播的直播间列表写入Redis
     * @param type
     */
    private void refreshDBToRedis(Integer type) {
        // 首先从数据库中查询正在直播的直播间列表
        List<LivingRoomRespDTO> resultList = livingRoomService.listAllLivingRoomFromDB(type);
        String cacheKey = cacheKeyBuilder.buildLivingRoomList(type);
        // 如果从数据库中查询到的集合为空
        if (CollectionUtils.isEmpty(resultList)) {
            redisTemplate.unlink(cacheKey);
            return;
        }
        // 不为空的话，写入Redis
        // 这里我们写入Redis的思路是，首先我们将所有数据写入一个名为cacheKey_temp的集合中，然后写入完成后直接修改cacheKey_temp的名字为cacheKey
        // 这样做可以避免我们在写入新数据之前删除旧数据后导致的数据读取不到的问题
        String tempListName = cacheKey + "_temp";
        // 需要一行一行push进去，pushAll方法有bug，会添加到一条记录里去
        for (LivingRoomRespDTO livingRoomRespDTO : resultList) {
            redisTemplate.opsForList().rightPush(tempListName, livingRoomRespDTO);
        }
        // 直接修改重命名这个list，不要直接对原来的list进行修改，减少阻塞的影响
        redisTemplate.rename(tempListName, cacheKey);
        redisTemplate.unlink(tempListName);
    }
}
