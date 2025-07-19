package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.gift.interfaces.ISkuStockInfoRpc;
import org.qiyu.live.gift.provider.dao.po.SkuStockInfoPO;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.qiyu.live.gift.provider.service.bo.DecrStockNumBO;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品库存相关操作rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class SkuStockInfoRpcImpl implements ISkuStockInfoRpc {


    @Resource
    private ISkuStockInfoService skuStockInfoService;


    @Resource
    private IAnchorShopInfoService anchorShopInfoService;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    // 乐观锁的最大尝试次数
    private final int MAX_TRY_TIMES = 5;


    /***
     * 根据商品skuId更新商品库存
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public boolean decrStockNumBySkuId(Long skuId, Integer num) {
        // 进行尝试更新库存
        for (int i = 0; i < MAX_TRY_TIMES; i++) {
            DecrStockNumBO decrStockNumBO = skuStockInfoService.decrStockNumBySkuId(skuId, num);
            // 如果是库存不足的话，直接返回，不再进行尝试
            if(decrStockNumBO.isNoStock()){
                return false;
            // 如果扣减库存成功
            } else if(decrStockNumBO.isSuccess()) {
                return true;
            }
        }
        return false;
    }


    /***
     * 预热当前主播的直播间的秒杀商品库存信息到Redis缓存中
     * @param anchorId
     * @return
     */
    @Override
    public boolean prepareStockInfo(Long anchorId) {
        // 根据主播id查询当前主播的所有带货商品skuId的集合
        List<Long> skuIdList = anchorShopInfoService.querySkuIdByAnchorId(anchorId);
        // 根据skuId的集合批量的查询出所有商品的库存
        List<SkuStockInfoPO> skuStockInfoPOS = skuStockInfoService.queryBySkuIds(skuIdList);
        // 将查询到的商品库存写入Redis中
        /*for (SkuStockInfoPO skuStockInfoPO : skuStockInfoPOS) {
            String cacheKey = cacheKeyBuilder.buildSkuStock(skuStockInfoPO.getSkuId());
            redisTemplate.opsForValue().set(cacheKey, skuStockInfoPO.getStockNum(),1, TimeUnit.DAYS);
        }*/
        // 使用multiset的方法，防止集合的大量数据导致Redis写入时卡顿等
        Map<String, Integer> cacheKeyMap = skuStockInfoPOS.stream()
                .collect(Collectors.toMap(skuStockInfoPO -> cacheKeyBuilder.buildSkuStock(skuStockInfoPO.getSkuId()), SkuStockInfoPO::getStockNum));
        redisTemplate.opsForValue().multiSet(cacheKeyMap);
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                for (String key : cacheKeyMap.keySet()) {
                    operations.expire((K) key, 1L, TimeUnit.DAYS);
                }
                return null;
            }
        });
        return true;
    }


    /***
     * 根据商品的skuId查询商品的缓存
     * @param skuId
     * @return
     */
    @Override
    public Integer queryStockNum(Long skuId) {
        // 直接从Redis中进行查询
        String cacheKey = cacheKeyBuilder.buildSkuStock(skuId);
        Object stockObj = redisTemplate.opsForValue().get(cacheKey);
        return stockObj == null ? null : (Integer) stockObj;
    }


    /***
     * 根据主播id同步Redis缓存中的商品库存信息到 MySQL中
     * @param anchorId
     * @return
     */
    @Override
    public boolean syncStockNumToMySQL(Long anchorId) {
        // 根据主播id查询当前主播的所有带货商品skuId的集合
        List<Long> skuIdList = anchorShopInfoService.querySkuIdByAnchorId(anchorId);
        for (Long skuId : skuIdList) {
            // 依次从缓存中获取到这些商品的缓存
            Integer stockNum = this.queryStockNum(skuId);
            if(stockNum!=null) {
                // 更新到MySQL中
                skuStockInfoService.updateStockNum(skuId, stockNum);
            }
        }
        return true;
    }


    /***
     * 根据商品skuId更新商品库存版本2
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public boolean decrStockNumBySkuIdV2(Long skuId, Integer num) {
        return skuStockInfoService.decrStockNumBySkuIdV2(skuId,num);
    }
}
