package org.qiyu.live.gift.interfaces;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品库存相关操作rpc接口
 * @Version: 1.0
 */
public interface ISkuStockInfoRpc {


    /***
     * 根据商品skuId更新商品库存
     * @param skuId
     * @param num
     * @return
     */
    boolean decrStockNumBySkuId(Long skuId,Integer num);


    /***
     * 预热当前主播的直播间的秒杀商品库存信息
     * @param anchorId
     * @return
     */
    boolean prepareStockInfo(Long anchorId);


    /***
     * 根据商品的skuId查询商品的缓存
     * @param skuId
     * @return
     */
    Integer queryStockNum(Long skuId);


    /***
     * 根据主播id同步Redis缓存中的商品库存信息到MySQL中
     * @param anchorId
     * @return
     */
    boolean syncStockNumToMySQL(Long anchorId);


    /***
     * 根据商品skuId更新商品库存版本2
     * @param skuId
     * @param num
     * @return
     */
    boolean decrStockNumBySkuIdV2(Long skuId,Integer num);
}
