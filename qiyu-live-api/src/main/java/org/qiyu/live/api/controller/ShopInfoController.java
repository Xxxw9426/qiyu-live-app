package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.IShopInfoService;
import org.qiyu.live.api.vo.req.PrepareOrderVO;
import org.qiyu.live.api.vo.req.ShopCarReqVO;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 商品带货相关操作controller层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/shop")
public class ShopInfoController {


    @Resource
    private IShopInfoService shopInfoService;


    /***
     * 根据直播间id查询当前主播带货商品sku的商品列表
     * @param roomId
     * @return
     */
    @PostMapping("/listSkuInfo")
    public WebResponseVO listSkuInfo(Integer roomId) {
        return WebResponseVO.success(shopInfoService.queryByRoomId(roomId));
    }


    /***
     * 根据商品skuId查询商品的详细信息
     * @param reqVO
     * @return
     */
    @PostMapping("detail")
    public WebResponseVO detail(SkuInfoReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.detail(reqVO));
    }


    /***
     * 向购物车中添加商品
     * @param reqVO
     * @return
     */
    @PostMapping("/addCar")
    public WebResponseVO addCar(ShopCarReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.addCar(reqVO));
    }


    /***
     * 从购物车中移除商品
     * @param reqVO
     * @return
     */
    @PostMapping("/removeFromCar")
    public WebResponseVO removeFromCar(ShopCarReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.removeFromCar(reqVO));
    }


    /***
     * 查询购物车中的商品清单
     * @param reqVO
     * @return
     */
    @PostMapping("/getCarInfo")
    public WebResponseVO getCarInfo(ShopCarReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.getCarInfo(reqVO));
    }


    /***
     * 清空购物车
     * @param reqVO
     * @return
     */
    @PostMapping("/clearCar")
    public WebResponseVO clearCar(ShopCarReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.clearShopCar(reqVO));
    }


    /***
     * 生成一个预支付订单
     * @param prepareOrderVO
     * @return
     */
    @PostMapping("/prepareOrder")
    public WebResponseVO prepareOrder(PrepareOrderVO prepareOrderVO) {
        return WebResponseVO.success(shopInfoService.prepareOrder(prepareOrderVO));
    }


    /***
     * 用户点击立即支付后后端根据订单信息扣减余额
     * @param prepareOrderVO
     * @return
     */
    @PostMapping("/payNow")
    public WebResponseVO payNow(PrepareOrderVO prepareOrderVO) {
        prepareOrderVO.setUserId(QiyuRequestContext.getUserId());
        return WebResponseVO.success(shopInfoService.payNow(prepareOrderVO));
    }
}
