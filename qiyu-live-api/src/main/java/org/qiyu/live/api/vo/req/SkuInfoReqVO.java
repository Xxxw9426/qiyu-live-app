package org.qiyu.live.api.vo.req;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 商品sku信息配置相关操作请求实体类
 * @Version: 1.0
 */

public class SkuInfoReqVO {

    private Long skuId;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    @Override
    public String toString() {
        return "SkuInfoReqVO{" +
                "skuId=" + skuId +
                '}';
    }
}
