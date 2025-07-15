package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.req.PayProductReqVO;
import org.qiyu.live.api.vo.resp.PayProductRespVO;
import org.qiyu.live.api.vo.resp.PayProductVO;
import org.qiyu.live.bank.dto.PayProductDTO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 钱包服务相关操作service接口
 * @Version: 1.0
 */

public interface IBankService {


    /***
     * 返回传入的type类型的付费产品实体类对象集合以及当前用户的账户余额
     * @param type
     * @return
     */
    PayProductVO products(Integer type);


    /***
     * 用户购买付费产品的支付请求
     * @param payProductReqVO
     * @return
     */
    PayProductRespVO payProduct(PayProductReqVO payProductReqVO);
}
