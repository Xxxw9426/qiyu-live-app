package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-19
 * @Description: 商品预支付订单商品列表信息实体类
 * @Version: 1.0
 */
public class SkuPrepareOrderItemInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6033305737226285944L;

    // 商品信息
    private SkuInfoDTO skuInfoDTO;

    // 商品数量
    private Integer count;

    public SkuInfoDTO getSkuInfoDTO() {
        return skuInfoDTO;
    }

    public void setSkuInfoDTO(SkuInfoDTO skuInfoDTO) {
        this.skuInfoDTO = skuInfoDTO;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "SkuPrepareOrderItemInfoDTO{" +
                "skuInfoDTO=" + skuInfoDTO +
                ", count=" + count +
                '}';
    }
}
