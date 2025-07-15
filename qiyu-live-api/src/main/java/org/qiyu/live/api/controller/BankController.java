package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.IBankService;
import org.qiyu.live.api.vo.req.PayProductReqVO;
import org.qiyu.live.api.vo.resp.PayProductVO;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.web.bind.annotation.*;
/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 钱包服务相关操作controller层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/bank")
public class BankController {


    @Resource
    private IBankService bankService;


    /***
     * 返回传入的type类型的付费产品实体类对象集合以及当前用户的账户余额
     * @param type
     * @return
     */
    @PostMapping("/products")
    public WebResponseVO products(Integer type) {
        ErrorAssert.isNotNull(type, BizBaseErrorEnum.PARAM_ERROR);
        PayProductVO productVO = bankService.products(type);
        return WebResponseVO.success(productVO);
    }


    /***
     * 用户购买付费产品的支付请求
     * @param payProductReqVO
     * @return
     */
    @PostMapping("/payProduct")
    public WebResponseVO payProduct(PayProductReqVO payProductReqVO) {
        // 首先进行参数校验
        return WebResponseVO.success(bankService.payProduct(payProductReqVO));
    }

}
