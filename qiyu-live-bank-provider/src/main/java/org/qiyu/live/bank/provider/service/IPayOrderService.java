package org.qiyu.live.bank.provider.service;

import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.provider.dao.po.PayOrderPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付订单相关操作service接口
 * @Version: 1.0
 */

public interface IPayOrderService {


    /***
     * 插入订单
     * @param payOrderPO
     * @return  订单id
     */
    String insertOne(PayOrderPO payOrderPO);


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


    /***
     * 根据orderId查询当前订单的详细信息
     * @param orderId
     * @return
     */
    PayOrderPO queryByOrderId(String orderId);
}
