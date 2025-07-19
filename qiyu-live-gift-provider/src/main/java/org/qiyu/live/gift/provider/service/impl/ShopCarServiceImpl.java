package org.qiyu.live.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.ShopCarItemRespDTO;
import org.qiyu.live.gift.dto.ShopCarReqDTO;
import org.qiyu.live.gift.dto.ShopCarRespDTO;
import org.qiyu.live.gift.dto.SkuInfoDTO;
import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;
import org.qiyu.live.gift.provider.service.IShopCarService;
import org.qiyu.live.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 购物车相关操作service接口实现类
 * @Version: 1.0
 */
@Service
public class ShopCarServiceImpl implements IShopCarService {


    @Resource
    private ISkuInfoService skuInfoService;


    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    /***
     * 向购物车中添加商品
     * @param reqDTO
     * @return
     */
    @Override
    public Boolean addCar(ShopCarReqDTO reqDTO) {
        // 因为我们的购物车是以直播间为维度的，当一场直播结束以后，与其有关的购物车就会消失，因此我们没有对其进行持久化，而是直接通过使用缓存来实现。
        // 这里我们采用map的结构，这个map的key是当前购物车中商品的skuId，value是商品的数量
        String cacheKey = cacheKeyBuilder.buildUserShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        redisTemplate.opsForHash().put(cacheKey,reqDTO.getSkuId(),1);
        return true;
    }


    /***
     * 从购物车中移除商品
     * @param reqDTO
     * @return
     */
    @Override
    public Boolean removeFromCar(ShopCarReqDTO reqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        redisTemplate.opsForHash().delete(cacheKey,reqDTO.getSkuId());
        return true;
    }


    /***
     * 查询购物车中的商品清单
     * @param reqDTO
     * @return
     */
    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO reqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        // 获取到全部的Redis中的当前购物车的商品信息列表
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(cacheKey);
        if (CollectionUtils.isEmpty(entries)) {
            return new ShopCarRespDTO();
        }
        // 将其转化为键为商品skuId，值为商品数量的map集合
        Map<Long, Integer> skuCountMap = new HashMap<>(entries.size());
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            skuCountMap.put(Long.valueOf((String) entry.getKey()), (Integer) entry.getValue());
        }
        // 根据刚才获取出来的商品id的集合查询商品的信息
        List<Long> skuIdList = new ArrayList<>(skuCountMap.keySet());
        List<SkuInfoPO> skuInfoPOS = skuInfoService.queryBySkuIds(skuIdList);
        // 设置返回的响应类
        ShopCarRespDTO shopCarRespDTO = new ShopCarRespDTO();
        shopCarRespDTO.setRoomId(reqDTO.getRoomId());
        shopCarRespDTO.setUserId(reqDTO.getUserId());
        // 设置响应类中的商品信息列表
        List<ShopCarItemRespDTO> itemList = new ArrayList<>();
        skuInfoPOS.forEach(skuInfoPO -> {
            ShopCarItemRespDTO item = new ShopCarItemRespDTO();
            item.setSkuInfoDTO(ConvertBeanUtils.convert(skuInfoPO, SkuInfoDTO.class));
            item.setCount(skuCountMap.get(skuInfoPO.getSkuId()));
            itemList.add(item);
        });
        shopCarRespDTO.setSkuCarItemRespDTOS(itemList);
        return shopCarRespDTO;
    }


    /***
     * 清空购物车
     * @param reqDTO
     * @return
     */
    @Override
    public Boolean clearShopCar(ShopCarReqDTO reqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }


    /***
     * 增加购物车中某个商品的数量
     * @param reqDTO
     * @return
     */
    @Override
    public Boolean addCarItemNum(ShopCarReqDTO reqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        redisTemplate.opsForHash().increment(cacheKey,reqDTO.getSkuId(),1);
        return null;
    }
}
