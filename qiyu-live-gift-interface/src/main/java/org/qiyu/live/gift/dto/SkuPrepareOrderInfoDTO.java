package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-19
 * @Description: 商品预支付订单信息实体类
 * @Version: 1.0
 */
public class SkuPrepareOrderInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8448477866498399167L;

    // 预支付订单的总金额
    private Integer totalPrice;

    // 预支付订单的商品列表
    private List<SkuPrepareOrderItemInfoDTO> skuPrepareOrderItemInfoDTOS;

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<SkuPrepareOrderItemInfoDTO> getSkuPrepareOrderItemInfoDTOS() {
        return skuPrepareOrderItemInfoDTOS;
    }

    public void setSkuPrepareOrderItemInfoDTOS(List<SkuPrepareOrderItemInfoDTO> skuPrepareOrderItemInfoDTOS) {
        this.skuPrepareOrderItemInfoDTOS = skuPrepareOrderItemInfoDTOS;
    }

    @Override
    public String toString() {
        return "SkuPrepareOrderInfoDTO{" +
                "totalPrice=" + totalPrice +
                ", skuPrepareOrderItemInfoDTOS=" + skuPrepareOrderItemInfoDTOS +
                '}';
    }
}
