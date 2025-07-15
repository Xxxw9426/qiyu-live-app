package org.qiyu.live.bank.api.service.impl;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.qiyu.live.bank.api.service.IPayNotifyService;
import org.qiyu.live.bank.api.vo.WxPayNotifyVO;
import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.interfaces.IPayOrderRpc;
import org.springframework.stereotype.Service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付回调请求相关处理service实现类
 * @Version: 1.0
 */
@Service
public class PayNotifyServiceImpl implements IPayNotifyService {


    @Resource
    private IPayOrderRpc payOrderRpc;


    /***
     * 处理支付回调请求的逻辑实现
     * @param paramJson
     * @return 处理成功后返回success
     */
    @Override
    public String notifyHandler(String paramJson) {
        WxPayNotifyVO wxPayNotifyVO = JSON.parseObject(paramJson, WxPayNotifyVO.class);
        PayOrderDTO payOrderDTO=new PayOrderDTO();
        payOrderDTO.setOrderId(wxPayNotifyVO.getOrderId());
        payOrderDTO.setUserId(wxPayNotifyVO.getUserId());
        payOrderDTO.setBizCode(wxPayNotifyVO.getBizCode());
        boolean notifySuccess= payOrderRpc.payNotify(payOrderDTO);
        if(notifySuccess){
            return "success";
        }
        return "fail";
    }
}
