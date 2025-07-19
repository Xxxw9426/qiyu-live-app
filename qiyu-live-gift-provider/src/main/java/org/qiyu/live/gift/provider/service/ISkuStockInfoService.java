package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.RollBackStockDTO;
import org.qiyu.live.gift.provider.dao.po.SkuStockInfoPO;
import org.qiyu.live.gift.provider.service.bo.DecrStockNumBO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品库存相关业务逻辑操作service接口
 * @Version: 1.0
 */
public interface ISkuStockInfoService {


    /***
     * 根据商品skuId更新商品库存
     * @param skuId
     * @param num
     * @return
     */
    DecrStockNumBO decrStockNumBySkuId(Long skuId, Integer num);


    /***
     * 根据skuId查询商品的库存信息
     * @param skuId
     * @return
     */
    SkuStockInfoPO queryBySkuId(Long skuId);


    /***
     * 根据传入的商品skuId的集合查询其对应的库存
     * @param skuIds
     * @return
     */
    List<SkuStockInfoPO> queryBySkuIds(List<Long> skuIds);


    /***
     * 更新商品库存为传入的num值
     * @param skuId
     * @param num
     * @return
     */
    boolean updateStockNum(Long skuId,Integer num);


    /***
     * 根据商品skuId更新商品库存版本2
     * @param skuId
     * @param num
     * @return
     */
    boolean decrStockNumBySkuIdV2(Long skuId, Integer num);


    /***
     * 根据商品的skuId的集合更新库存版本3
     * @param skuIdList
     * @param num
     * @return
     */
    boolean decrStockNumBySkuIdV3(List<Long> skuIdList, Integer num);


    /***
     * 库存回滚操作处理器
     * @param rollBackStockDTO
     */
    void stockRollBackHandler(RollBackStockDTO rollBackStockDTO);
}
