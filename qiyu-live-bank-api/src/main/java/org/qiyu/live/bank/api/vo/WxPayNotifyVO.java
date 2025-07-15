package org.qiyu.live.bank.api.vo;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 微信支付回调数据转化实体类
 * @Version: 1.0
 */

public class WxPayNotifyVO {

    // 订单id
    private String orderId;

    // 用户id
    private Long userId;

    // 业务码
    private Integer bizCode;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getBizCode() {
        return bizCode;
    }

    public void setBizCode(Integer bizCode) {
        this.bizCode = bizCode;
    }

    @Override
    public String toString() {
        return "WxPayNotifyVO{" +
                "orderId='" + orderId + '\'' +
                ", userId=" + userId +
                ", bizCode=" + bizCode +
                '}';
    }
}
