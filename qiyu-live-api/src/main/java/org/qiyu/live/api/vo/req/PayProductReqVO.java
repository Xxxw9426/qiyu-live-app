package org.qiyu.live.api.vo.req;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 用户购买付费产品的支付请求实体类
 * @Version: 1.0
 */

public class PayProductReqVO {

    // 产品id
    private Integer productId;

    // 支付来源(直播间，个人中心，聊天页面，广告弹窗引导，第三方宣传页面)
    private Integer paySource;

    // 支付渠道
    private Integer payChannel;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getPaySource() {
        return paySource;
    }

    public void setPaySource(Integer paySource) {
        this.paySource = paySource;
    }

    public Integer getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(Integer payChannel) {
        this.payChannel = payChannel;
    }

    @Override
    public String toString() {
        return "PayProductReqVO{" +
                "productId=" + productId +
                ", paySource=" + paySource +
                ", payChannel=" + payChannel +
                '}';
    }
}
