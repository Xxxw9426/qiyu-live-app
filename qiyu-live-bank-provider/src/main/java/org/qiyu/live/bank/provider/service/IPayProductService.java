package org.qiyu.live.bank.provider.service;

import org.qiyu.live.bank.dto.PayProductDTO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 付费产品相关操作service接口
 * @Version: 1.0
 */

public interface IPayProductService {


    /***
     * 返回传入的type类型的付费产品实体类对象集合
     * @param type
     * @return
     */
    List<PayProductDTO> products(Integer type);


    /***
     * 根据付费产品id查询付费产品实体类对象
     * @param productId
     * @return
     */
    PayProductDTO getByProductId(Integer productId);
}
