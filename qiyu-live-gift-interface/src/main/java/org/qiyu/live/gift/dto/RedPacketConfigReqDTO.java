package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-18
 * @Description: 直播间红包雨服务红包雨相关配置数据请求实体类
 * @Version: 1.0
 */

public class RedPacketConfigReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8647742827215810362L;

    // 主播id
    private Long anchorId;

    // 直播间id
    private Integer roomId;

    // 红包雨总金额
    private Integer totalPrice;

    // 红包雨总数量
    private Integer totalCount;

    // 当前领取红包的用户id
    private Long userId;

    private Integer status;

    private String remark;

    // 红包雨红包数据配置唯一code
    private String redPacketConfigCode;

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRedPacketConfigCode() {
        return redPacketConfigCode;
    }

    public void setRedPacketConfigCode(String redPacketConfigCode) {
        this.redPacketConfigCode = redPacketConfigCode;
    }

    @Override
    public String toString() {
        return "RedPacketConfigReqDTO{" +
                "anchorId=" + anchorId +
                ", roomId=" + roomId +
                ", totalPrice=" + totalPrice +
                ", totalCount=" + totalCount +
                ", userId=" + userId +
                ", status=" + status +
                ", remark='" + remark + '\'' +
                ", redPacketConfigCode='" + redPacketConfigCode + '\'' +
                '}';
    }
}
