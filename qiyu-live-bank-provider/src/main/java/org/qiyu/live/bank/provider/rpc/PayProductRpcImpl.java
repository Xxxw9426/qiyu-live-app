package org.qiyu.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.bank.dto.PayProductDTO;
import org.qiyu.live.bank.interfaces.IPayProductRpc;
import org.qiyu.live.bank.provider.service.IPayProductService;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 付费产品相关操作rpc接口实现类
 * @Version: 1.0
 */

@DubboService
public class PayProductRpcImpl implements IPayProductRpc {


    @Resource
    private IPayProductService payProductService;


    /***
     * 返回传入的type类型的付费产品实体类对象集合
     * @param type
     * @return
     */
    @Override
    public List<PayProductDTO> products(Integer type) {
        return payProductService.products(type);
    }


    /***
     * 根据付费产品id查询付费产品实体类对象
     * @param productId
     * @return
     */
    @Override
    public PayProductDTO getByProductId(Integer productId) {
        return payProductService.getByProductId(productId);
    }
}
