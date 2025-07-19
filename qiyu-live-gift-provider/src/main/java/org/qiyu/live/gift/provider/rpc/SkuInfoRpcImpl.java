package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.SkuDetailInfoDTO;
import org.qiyu.live.gift.dto.SkuInfoDTO;
import org.qiyu.live.gift.interfaces.ISkuInfoRpc;
import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.qiyu.live.gift.provider.service.ISkuInfoService;
import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 商品sku信息相关操作rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class SkuInfoRpcImpl implements ISkuInfoRpc {


    @Resource
    private ISkuInfoService skuInfoService;


    @Resource
    private IAnchorShopInfoService anchorShopInfoService;


    /***
     * 根据主播id查询其带货商品sku的商品列表
     * @param anchorId
     * @return
     */
    @Override
    public List<SkuInfoDTO> queryByAnchorId(Long anchorId) {
        // 首先查询当前主播带货商品sku的Id的集合
        List<Long> skuIdList = anchorShopInfoService.querySkuIdByAnchorId(anchorId);
        // 再根据skuId集合查询其对应的商品sku商品列表
        List<SkuInfoPO> skuInfoPOS = skuInfoService.queryBySkuIds(skuIdList);
        return ConvertBeanUtils.convertList(skuInfoPOS, SkuInfoDTO.class);
    }


    /***
     * 根据商品skuId查询商品详情信息
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailInfoDTO queryBySkuId(Long skuId) {
        SkuInfoPO skuInfoPO = skuInfoService.queryBySkuIdFromCache(skuId);
        return ConvertBeanUtils.convert(skuInfoPO, SkuDetailInfoDTO.class);
    }
}
