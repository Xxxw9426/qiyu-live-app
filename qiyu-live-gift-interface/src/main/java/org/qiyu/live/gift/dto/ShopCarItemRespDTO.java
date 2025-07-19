package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 购物车商品列表信息响应实体类
 * @Version: 1.0
 */
public class ShopCarItemRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8164388472495002982L;

    // 商品个数
    private Integer count;

    // 商品信息
    private SkuInfoDTO skuInfoDTO;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public SkuInfoDTO getSkuInfoDTO() {
        return skuInfoDTO;
    }

    public void setSkuInfoDTO(SkuInfoDTO skuInfoDTO) {
        this.skuInfoDTO = skuInfoDTO;
    }

    @Override
    public String toString() {
        return "ShopCarItemRespDTO{" +
                "count=" + count +
                ", skuInfoDTO=" + skuInfoDTO +
                '}';
    }
}
