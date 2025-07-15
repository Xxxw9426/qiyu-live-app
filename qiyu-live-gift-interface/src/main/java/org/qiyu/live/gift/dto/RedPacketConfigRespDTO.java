package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-18
 * @Description: 直播间红包雨服务红包雨相关配置数据响应实体类
 * @Version: 1.0
 */

public class RedPacketConfigRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2644248917589898574L;

    // 主播id
    private Long anchorId;

    // 红包雨总金额
    private Integer totalPrice;

    // 红包雨总数量
    private Integer totalCount;

    // 红包雨红包数据配置的唯一code
    private String configCode;

    private String remark;

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "RedPacketConfigRespDTO{" +
                "anchorId=" + anchorId +
                ", totalPrice=" + totalPrice +
                ", totalCount=" + totalCount +
                ", configCode='" + configCode + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
