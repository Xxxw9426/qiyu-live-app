package org.qiyu.live.api.vo.resp;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 用户购买服务产品的支付请求响应实体类
 * @Version: 1.0
 */

public class PayProductRespVO {

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
