package org.qiyu.live.bank.interfaces;

import org.qiyu.live.bank.dto.PayOrderDTO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付订单相关操作rpc接口
 * @Version: 1.0
 */

public interface IPayOrderRpc {


    /***
     * 插入订单
     * @param payOrderDTO
     * @return  订单id
     */
    String insertOne(PayOrderDTO payOrderDTO);


    /***
     * 根据主键id更新订单状态
     * @param id
     * @param status
     * @return
     */
    boolean updateOrderStatus(Long id, Integer status);


    /***
     * 根据orderId更新订单状态
     * @param orderId
     * @param status
     * @return
     */
    boolean updateOrderStatus(String orderId, Integer status);


    /***
     * 支付回调请求处理逻辑
     * @param payOrderDTO
     * @return
     */
    boolean payNotify(PayOrderDTO payOrderDTO);
}
