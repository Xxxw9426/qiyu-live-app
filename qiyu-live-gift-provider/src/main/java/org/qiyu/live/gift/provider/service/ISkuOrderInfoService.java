package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.SkuOrderInfoReqDTO;
import org.qiyu.live.gift.dto.SkuOrderInfoRespDTO;
import org.qiyu.live.gift.provider.dao.po.SkuOrderInfoPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品订单相关业务逻辑操作service接口
 * @Version: 1.0
 */
public interface ISkuOrderInfoService {


    /***
     * 根据用户id和直播间id查询当前用户在当前直播间的商品订单
     * @param userId
     * @param roomId
     * @return
     */
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);


    /***
     * 根据订单的orderId查询订单信息
     * @param orderId
     * @return
     */
    SkuOrderInfoRespDTO queryByOrderId(Long orderId);


    /***
     * 插入一条商品订单信息
     * @param reqDTO
     * @return
     */
    SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO reqDTO);


    /***
     * 根据商品订单的id修改商品订单的状态
     * @param reqDTO
     * @return
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO reqDTO);
}
