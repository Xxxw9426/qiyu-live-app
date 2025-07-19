package org.qiyu.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.error.ApiErrorEnum;
import org.qiyu.live.api.service.IShopInfoService;
import org.qiyu.live.api.vo.req.PrepareOrderVO;
import org.qiyu.live.api.vo.req.ShopCarReqVO;
import org.qiyu.live.api.vo.req.SkuInfoReqVO;
import org.qiyu.live.api.vo.resp.ShopCarRespVO;
import org.qiyu.live.api.vo.resp.SkuDetailInfoVO;
import org.qiyu.live.api.vo.resp.SkuInfoVO;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.*;
import org.qiyu.live.gift.interfaces.IShopCarRpc;
import org.qiyu.live.gift.interfaces.ISkuInfoRpc;
import org.qiyu.live.gift.interfaces.ISkuOrderInfoRpc;
import org.qiyu.live.living.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.ILivingRoomRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 商品带货相关操作service层接口实现类
 * @Version: 1.0
 */
@Service
public class ShopInfoServiceImpl implements IShopInfoService {


    @DubboReference
    private ISkuInfoRpc skuInfoRpc;


    @DubboReference
    private IShopCarRpc shopCarRpc;


    @DubboReference
    private ILivingRoomRpc livingRoomRpc;


    @DubboReference
    private ISkuOrderInfoRpc skuOrderInfoRpc;


    /***
     * 根据直播间id查询直播间主播带货商品sku的商品列表
     * @param roomId
     * @return
     */
    @Override
    public List<SkuInfoVO> queryByRoomId(Integer roomId) {
        // 首先根据直播间id查询主播id
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(livingRoomRespDTO, BizBaseErrorEnum.PARAM_ERROR);
        Long anchorId = livingRoomRespDTO.getAnchorId();
        // 通过rpc调用通过主播id查询带货商品sku商品列表
        List<SkuInfoDTO> skuInfoDTOS = skuInfoRpc.queryByAnchorId(anchorId);
        ErrorAssert.isTrue(!CollectionUtils.isEmpty(skuInfoDTOS), BizBaseErrorEnum.PARAM_ERROR);
        return ConvertBeanUtils.convertList(skuInfoDTOS, SkuInfoVO.class);
    }


    /***
     * 根据商品的skuId查询商品的详细信息
     * @param skuInfoReqVO
     * @return
     */
    @Override
    public SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO) {
        return ConvertBeanUtils.convert(skuInfoRpc.queryBySkuId(skuInfoReqVO.getSkuId()), SkuDetailInfoVO.class);
    }


    /***
     * 向购物车中添加商品
     * @param reqVO
     * @return
     */
    @Override
    public Boolean addCar(ShopCarReqVO reqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(reqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(QiyuRequestContext.getUserId());
        return shopCarRpc.addCar(shopCarReqDTO);
    }


    /***
     * 从购物车中移除商品
     * @param reqVO
     * @return
     */
    @Override
    public Boolean removeFromCar(ShopCarReqVO reqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(reqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(QiyuRequestContext.getUserId());
        return shopCarRpc.removeFromCar(shopCarReqDTO);
    }


    /***
     * 查询购物车中的商品清单
     * @param reqVO
     * @return
     */
    @Override
    public ShopCarRespVO getCarInfo(ShopCarReqVO reqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(reqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(QiyuRequestContext.getUserId());
        return ConvertBeanUtils.convert(shopCarRpc.getCarInfo(shopCarReqDTO), ShopCarRespVO.class);
    }


    /***
     * 清空购物车
     * @param reqVO
     * @return
     */
    @Override
    public Boolean clearShopCar(ShopCarReqVO reqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(reqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(QiyuRequestContext.getUserId());
        return shopCarRpc.clearShopCar(shopCarReqDTO);
    }


    /***
     * 生成一条预支付订单
     * @param prepareOrderVO
     * @return
     */
    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO prepareOrderVO) {
        PrepareOrderReqDTO reqDTO=new PrepareOrderReqDTO();
        reqDTO.setUserId(QiyuRequestContext.getUserId());
        reqDTO.setRoomId(prepareOrderVO.getRoomId());
        return skuOrderInfoRpc.prepareOrder(reqDTO);
    }


    /***
     * 用户点击立即支付后后端根据订单信息扣减余额
     * @param prepareOrderVO
     * @return
     */
    @Override
    public boolean payNow(PrepareOrderVO prepareOrderVO) {
       boolean status=skuOrderInfoRpc.payNow(ConvertBeanUtils.convert(prepareOrderVO, PayNowReqDTO.class));
       ErrorAssert.isTrue(status, ApiErrorEnum.PRICE_NOT_ENOUGH);
       return status;
    }
}
