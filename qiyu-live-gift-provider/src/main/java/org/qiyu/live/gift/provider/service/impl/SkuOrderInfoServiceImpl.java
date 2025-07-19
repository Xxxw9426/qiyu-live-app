package org.qiyu.live.gift.provider.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.SkuOrderInfoRespDTO;
import org.qiyu.live.gift.provider.dao.mapper.ISkuOrderInfoMapper;
import org.qiyu.live.gift.provider.dao.po.SkuOrderInfoPO;
import org.qiyu.live.gift.provider.service.ISkuOrderInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品订单信息相关业务逻辑操作service接口实现类
 * @Version: 1.0
 */
@Service
public class SkuOrderInfoServiceImpl implements ISkuOrderInfoService {


    @Resource
    private ISkuOrderInfoMapper skuOrderInfoMapper;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    /***
     * 根据用户id和直播间id查询当前用户在当前直播间的商品订单
     * @param userId
     * @param roomId
     * @return
     */
    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        // 首先从Redis的缓存中进行查询
        String cacheKey = cacheKeyBuilder.buildSkuOrder(userId, roomId);
        Object cacheObj = redisTemplate.opsForValue().get(cacheKey);
        // 缓存命中的话直接返回
        if(cacheObj != null) {
            return ConvertBeanUtils.convert(cacheObj, SkuOrderInfoRespDTO.class);
        }
        // 缓存未命中，从数据库中进行查询
        LambdaQueryWrapper<SkuOrderInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuOrderInfoPO::getUserId, userId);
        queryWrapper.eq(SkuOrderInfoPO::getRoomId, roomId);
        queryWrapper.orderByDesc(SkuOrderInfoPO::getId);
        queryWrapper.last("limit 1");
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoMapper.selectOne(queryWrapper);
        // 数据库中查询到了写入缓存
        if(skuOrderInfoPO != null){
            redisTemplate.opsForValue().set(cacheKey, skuOrderInfoPO,1, TimeUnit.HOURS);
            return ConvertBeanUtils.convert(skuOrderInfoPO, SkuOrderInfoRespDTO.class);
        }
        return null;
    }


    /***
     * 根据订单的orderId查询订单信息
     * @param orderId
     * @return
     */
    @Override
    public SkuOrderInfoRespDTO queryByOrderId(Long orderId) {
        // 首先从缓存中获取
        String cacheKey = cacheKeyBuilder.buildSkuOrderInfo(orderId);
        Object cacheObj = redisTemplate.opsForValue().get(cacheKey);
        // 缓存命中的话直接返回
        if(cacheObj != null) {
            return ConvertBeanUtils.convert(cacheObj, SkuOrderInfoRespDTO.class);
        }
        // 缓存未命中，从数据库中进行查询
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoMapper.selectById(orderId);
        // 数据库中查询到了写入缓存
        if(skuOrderInfoPO != null){
            SkuOrderInfoRespDTO sku = ConvertBeanUtils.convert(skuOrderInfoPO, SkuOrderInfoRespDTO.class);
            redisTemplate.opsForValue().set(cacheKey, sku,1, TimeUnit.HOURS);
            return sku;
        }
        return null;
    }


    /***
     * 插入一条商品订单信息
     * @param reqDTO
     * @return
     */
    @Override
    public SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO reqDTO) {
        // hutool工具包的StrUtil
        String skuIdListStr = StrUtil.join(",", reqDTO.getSkuIdList());
        SkuOrderInfoPO skuOrderInfoPO = ConvertBeanUtils.convert(reqDTO, SkuOrderInfoPO.class);
        skuOrderInfoPO.setSkuIdList(skuIdListStr);
        skuOrderInfoMapper.insert(skuOrderInfoPO);
        return skuOrderInfoPO;
    }


    /***
     * 根据商品订单的id修改商品订单的状态
     * @param reqDTO
     * @return
     */
    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO reqDTO) {
        // 修改订单状态
        SkuOrderInfoPO skuOrderInfoPO = new SkuOrderInfoPO();
        skuOrderInfoPO.setStatus(reqDTO.getStatus());
        skuOrderInfoPO.setId(reqDTO.getId());
        skuOrderInfoMapper.updateById(skuOrderInfoPO);
        // 删除Redis中的之前的商品订单的缓存
        String cacheKey = cacheKeyBuilder.buildSkuOrder(reqDTO.getUserId(), reqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }
}
