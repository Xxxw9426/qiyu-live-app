package org.qiyu.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.gift.provider.dao.mapper.ISkuInfoMapper;
import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;
import org.qiyu.live.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 商品sku信息相关业务逻辑操作service接口实现类
 * @Version: 1.0
 */
@Service
public class SkuInfoServiceImpl implements ISkuInfoService {


    @Resource
    private ISkuInfoMapper skuInfoMapper;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    /***
     * 根据skuId的集合查询该集合中对应的sku商品的商品列表
     * @param skuIdList
     * @return
     */
    @Override
    public List<SkuInfoPO> queryBySkuIds(List<Long> skuIdList) {
        LambdaQueryWrapper<SkuInfoPO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(SkuInfoPO::getSkuId,skuIdList);
        queryWrapper.eq(SkuInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        return skuInfoMapper.selectList(queryWrapper);
    }


    /***
     * 根据商品skuId查询商品的详情信息
     * @param skuId
     * @return
     */
    @Override
    public SkuInfoPO queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuInfoPO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(SkuInfoPO::getSkuId,skuId);
        queryWrapper.eq(SkuInfoPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return skuInfoMapper.selectOne(queryWrapper);
    }


    /***
     * 从缓存中根据商品skuId查询商品详情信息
     * @param skuId
     * @return
     */
    @Override
    public SkuInfoPO queryBySkuIdFromCache(Long skuId) {
        // 从缓存中获取当前skuId对应的商品详情信息
        String detailKey=cacheKeyBuilder.buildSkuDetail(skuId);
        Object skuInfoCacheObj = redisTemplate.opsForValue().get(detailKey);
        // 查询到直接返回
        if(skuInfoCacheObj != null) {
            return (SkuInfoPO) skuInfoCacheObj;
        }
        // 没有查询到的话则从数据库进行查询
        SkuInfoPO skuInfoPO = this.queryBySkuId(skuId);
        // 数据库也没有查询到
        if(skuInfoPO==null) {
            return null;
        }
        // 数据库查询到，写入缓存
        redisTemplate.opsForValue().set(detailKey, skuInfoPO,1, TimeUnit.DAYS);
        return skuInfoPO;
    }
}
