package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.SkuDetailInfoDTO;
import org.qiyu.live.gift.dto.SkuInfoDTO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 商品sku信息相关操作rpc接口
 * @Version: 1.0
 */
public interface ISkuInfoRpc {


    /***
     * 根据主播id查询其带货商品sku的商品列表
     * @param anchorId
     * @return
     */
    List<SkuInfoDTO> queryByAnchorId(Long anchorId);


    /***
     * 根据商品skuId查询商品详情信息
     * @param skuId
     * @return
     */
    SkuDetailInfoDTO queryBySkuId(Long skuId);
}
