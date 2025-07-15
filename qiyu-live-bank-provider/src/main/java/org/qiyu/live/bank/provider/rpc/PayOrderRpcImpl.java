package org.qiyu.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.interfaces.IPayOrderRpc;
import org.qiyu.live.bank.provider.dao.po.PayOrderPO;
import org.qiyu.live.bank.provider.service.IPayOrderService;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付订单相关操作rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class PayOrderRpcImpl implements IPayOrderRpc {


    @Resource
    private IPayOrderService payOrderService;


    /***
     * 插入订单
     * @param payOrderDTO
     * @return 订单id
     */
    @Override
    public String insertOne(PayOrderDTO payOrderDTO) {
        return payOrderService.insertOne(ConvertBeanUtils.convert(payOrderDTO, PayOrderPO.class));
    }


    /***
     * 根据主键id更新订单状态
     * @param id
     * @param status
     * @return
     */
    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        return payOrderService.updateOrderStatus(id, status);
    }


    /***
     * 根据orderId更新订单状态
     * @param orderId
     * @param status
     * @return
     */
    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        return payOrderService.updateOrderStatus(orderId, status);
    }


    /***
     * 支付回调请求处理逻辑
     * @param payOrderDTO
     * @return
     */
    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        return payOrderService.payNotify(payOrderDTO);
    }
}
