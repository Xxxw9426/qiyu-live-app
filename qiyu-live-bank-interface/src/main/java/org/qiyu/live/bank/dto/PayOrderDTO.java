package org.qiyu.live.bank.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付订单相关数据实体类
 * @Version: 1.0
 */

public class PayOrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6978677010169162147L;

    // 主键id
    private Long id;

    // 订单id
    private String orderId;

    // 产品id
    private Integer productId;

    // 用户id
    private Long userId;

    // 业务码
    private Integer bizCode;

    // 支付来源
    private Integer source;

    // 支付渠道(支付宝，微信，银联，收银台)
    private Integer payChannel;

    // 支付状态(待支付，支付中，已支付，撤销，无效)
    private Integer status;

    // 支付时间(以回调处理时间为准)
    private Date payTime;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(Integer payChannel) {
        this.payChannel = payChannel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "PayOrderDTO{" +
                "id=" + id +
                ", orderId='" + orderId + '\'' +
                ", productId=" + productId +
                ", userId=" + userId +
                ", bizCode=" + bizCode +
                ", source=" + source +
                ", payChannel=" + payChannel +
                ", status=" + status +
                ", payTime=" + payTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
