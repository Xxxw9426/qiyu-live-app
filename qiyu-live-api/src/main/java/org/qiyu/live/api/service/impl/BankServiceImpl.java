package org.qiyu.live.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.IBankService;
import org.qiyu.live.api.vo.req.PayProductReqVO;
import org.qiyu.live.api.vo.resp.PayProductItemVO;
import org.qiyu.live.api.vo.resp.PayProductRespVO;
import org.qiyu.live.api.vo.resp.PayProductVO;
import org.qiyu.live.bank.constants.OrderStatusEnum;
import org.qiyu.live.bank.constants.PaySourceEnum;
import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.dto.PayProductDTO;
import org.qiyu.live.bank.interfaces.IPayOrderRpc;
import org.qiyu.live.bank.interfaces.IPayProductRpc;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 钱包服务相关操作service接口实现类
 * @Version: 1.0
 */
@Service
public class BankServiceImpl implements IBankService {


    @DubboReference
    private IPayProductRpc payProductRpc;


    @DubboReference
    private IPayOrderRpc payOrderRpc;


    @Resource
    private RestTemplate restTemplate;


    @Resource
    private IQiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;


    /***
     * 返回传入的type类型的付费产品实体类对象集合以及当前用户的账户余额
     * @param type
     * @return
     */
    public PayProductVO products(Integer type){
        PayProductVO payProductVO = new PayProductVO();
        // 首先获取付费产品相关信息实体类对象集合
        List<PayProductDTO> products = payProductRpc.products(type);
        List<PayProductItemVO> itemVOList=new ArrayList<>();
        for (PayProductDTO product : products) {
            PayProductItemVO vo = new PayProductItemVO();
            vo.setName(product.getName());
            vo.setId(product.getId());
            vo.setCoinNum(JSON.parseObject(product.getExtra()).getInteger("coin"));
            itemVOList.add(vo);
        }
        payProductVO.setPayProductItems(itemVOList);
        // 然后获取当前用户的账户余额
        payProductVO.setCurrentBalance(qiyuCurrencyAccountRpc.getBalance(QiyuRequestContext.getUserId()));
        return payProductVO;
    }


    /***
     * 用户购买付费产品的支付请求
     * @param payProductReqVO
     * @return
     */
    @Override
    public PayProductRespVO payProduct(PayProductReqVO payProductReqVO) {
        // 校验传入的参数
        ErrorAssert.isTrue(payProductReqVO!=null && payProductReqVO.getPaySource() !=null && payProductReqVO.getProductId()!=null, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isNotNull(PaySourceEnum.find(payProductReqVO.getPaySource()),BizBaseErrorEnum.PARAM_ERROR);
        // 校验当前用户要购买的付费产品
        PayProductDTO payProductDTO = payProductRpc.getByProductId(payProductReqVO.getProductId());
        ErrorAssert.isNotNull(payProductDTO, BizBaseErrorEnum.PARAM_ERROR);
        // 插入一条待支付状态的订单
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setProductId(payProductReqVO.getProductId());
        payOrderDTO.setUserId(QiyuRequestContext.getUserId());
        payOrderDTO.setSource(payProductReqVO.getPaySource());
        payOrderDTO.setPayChannel(payProductReqVO.getPayChannel());
        String orderId = payOrderRpc.insertOne(payOrderDTO);
        // 更新订单状态为支付中状态
        payOrderRpc.updateOrderStatus(orderId,OrderStatusEnum.PAYING.getCode());
        PayProductRespVO respVO = new PayProductRespVO();
        respVO.setOrderId(orderId);
        // todo 发起一个远程http请求 restTemplate-> 模拟第三方支付平台发起支付回调
        // TODO 这里应该是支付成功后吗，由第三方支付所做的事情，因为我们是模拟支付，所以我们直接发起支付成功后的回调请求：
        com.alibaba.fastjson2.JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderId);
        jsonObject.put("userId", QiyuRequestContext.getUserId());
        jsonObject.put("bizCode", 10001);
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("param", jsonObject.toJSONString());
        // 使用RestTemplate进行HTTP的发送
        ResponseEntity<String> resultEntity = restTemplate.postForEntity("http://localhost:8201/live/bank/payNotify/wxNotify?param={param}", null, String.class, paramMap);
        System.out.println(resultEntity.getBody());
        return respVO;
    }
}
