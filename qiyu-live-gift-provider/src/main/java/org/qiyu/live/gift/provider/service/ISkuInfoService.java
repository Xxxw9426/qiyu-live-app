package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 商品sku信息相关业务逻辑操作service接口
 * @Version: 1.0
 */

public interface ISkuInfoService {


    /***
     * 根据skuId的集合查询该集合中对应的sku商品的商品列表
     * @param skuIdList
     * @return
     */
    List<SkuInfoPO> queryBySkuIds(List<Long> skuIdList);


    /***
     * 根据商品skuId查询商品的详情信息
     * @param skuId
     * @return
     */
    SkuInfoPO queryBySkuId(Long skuId);


    /***
     * 从缓存中根据商品skuId查询商品详情信息
     * @param skuId
     * @return
     */
    SkuInfoPO queryBySkuIdFromCache(Long skuId);
}
