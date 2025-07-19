package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.*;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品订单相关操作rpc接口
 * @Version: 1.0
 */
public interface ISkuOrderInfoRpc {


    /***
     * 根据用户id和直播间id查询当前用户在当前直播间的商品订单
     * @param userId
     * @param roomId
     * @return
     */
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);


    /***
     * 插入一条商品订单信息
     * @param reqDTO
     * @return
     */
    boolean insertOne(SkuOrderInfoReqDTO reqDTO);


    /***
     * 根据商品订单的id修改商品订单的状态
     * @param reqDTO
     * @return
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO reqDTO);


    /***
     * 生成一条预支付订单
     * @param reqDTO
     * @return
     */
    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO reqDTO);


    /***
     * 用户点击立即支付后后端根据订单信息扣减余额
     * @param payNowReqDTO
     * @return
     */
    boolean payNow(PayNowReqDTO payNowReqDTO);
}
