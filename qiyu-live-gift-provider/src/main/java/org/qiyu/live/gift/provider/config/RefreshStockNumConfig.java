package org.qiyu.live.gift.provider.config;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 用于定时将Redis中的商品缓存更新到MySQL中的类
 * @Version: 1.0
 */
@Configuration
public class RefreshStockNumConfig implements InitializingBean {


    @DubboReference
    private ISkuStockInfoRpc skuStockInfoRpc;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private IAnchorShopInfoService anchorShopInfoService;


    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    // 延迟线程池，每隔1秒就会执行一次提交给该线程池的任务
    private static final ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1);


    @Override
    public void afterPropertiesSet() throws Exception {
        // 每一秒钟刷新一次直播间商品的库存的Redis缓存到MySQL中
        SCHEDULED_THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(new RefreshCacheListJob(), 3000, 15000, TimeUnit.MILLISECONDS);
    }


    /**
     * 线程任务：执行更新操作
     */
    class RefreshCacheListJob implements Runnable {
        @Override
        public void run() {
            Boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKeyBuilder.buildStockSyncLock(), 1, 14, TimeUnit.SECONDS);
            if(lockStatus){
                // 查询到所有主播的id的集合
                List<Long> anchorIdList = anchorShopInfoService.queryAllValidAnchorId();
                for(Long anchorId : anchorIdList) {
                    skuStockInfoRpc.syncStockNumToMySQL(anchorId);
                }
            }
        }
    }
}
