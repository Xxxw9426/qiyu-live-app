package org.qiyu.live.api.vo.resp;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 商品sku详情信息配置响应实体类对象
 * @Version: 1.0
 */
public class SkuDetailInfoVO {

    // skuId
    private Long skuId;

    // 价格
    private Integer skuPrice;

    // 编码
    private String skuCode;

    // 商品名
    private String name;

    // 商品缩略图
    private String iconUrl;

    // 商品原图
    private String originalIconUrl;

    // 备注
    private String remark;

    //还有其它复杂数据

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(Integer skuPrice) {
        this.skuPrice = skuPrice;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getOriginalIconUrl() {
        return originalIconUrl;
    }

    public void setOriginalIconUrl(String originalIconUrl) {
        this.originalIconUrl = originalIconUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "SkuDetailInfoVO{" +
                "skuId=" + skuId +
                ", skuPrice=" + skuPrice +
                ", skuCode='" + skuCode + '\'' +
                ", name='" + name + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", originalIconUrl='" + originalIconUrl + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
