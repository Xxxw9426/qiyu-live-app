package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.req.PrepareOrderVO;
import org.qiyu.live.api.vo.req.ShopCarReqVO;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.api.vo.resp.ShopCarRespVO;
import org.qiyu.live.api.vo.resp.SkuDetailInfoVO;
import org.qiyu.live.api.vo.resp.SkuInfoVO;
import org.qiyu.live.gift.dto.SkuPrepareOrderInfoDTO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 商品带货相关操作service层接口
 * @Version: 1.0
 */
public interface IShopInfoService {


    /***
     * 根据直播间id查询直播间主播带货商品sku的商品列表
     * @param roomId
     * @return
     */
    List<SkuInfoVO> queryByRoomId(Integer roomId);


    /***
     * 根据商品的skuId查询商品的详细信息
     * @param skuInfoReqVO
     * @return
     */
    SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO);


    /***
     * 向购物车中添加商品
     * @param reqVO
     * @return
     */
    Boolean addCar(ShopCarReqVO reqVO);


    /***
     * 从购物车中移除商品
     * @param reqVO
     * @return
     */
    Boolean removeFromCar(ShopCarReqVO reqVO);


    /***
     * 查询购物车中的商品清单
     * @param reqVO
     * @return
     */
    ShopCarRespVO getCarInfo(ShopCarReqVO reqVO);


    /***
     * 清空购物车
     * @param reqVO
     * @return
     */
    Boolean clearShopCar(ShopCarReqVO reqVO);


    /***
     * 生成一条预支付订单
     * @param prepareOrderVO
     * @return
     */
    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO prepareOrderVO);


    /***
     * 用户点击立即支付后后端根据订单信息扣减余额
     * @param prepareOrderVO
     * @return
     */
    boolean payNow(PrepareOrderVO prepareOrderVO);

}
