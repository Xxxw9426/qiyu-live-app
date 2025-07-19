package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品订单相关操作响应实体类
 * @Version: 1.0
 */
public class SkuOrderInfoRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4919004573498816942L;

    // 订单id
    private Long Id;

    // 下单商品id字符串
    private String skuIdList;

    // 用户id
    private Long userId;

    // 直播间id
    private Integer roomId;

    // 订单状态
    private Integer status;

    // 拓展字段
    private String extra;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getSkuIdList() {
        return skuIdList;
    }

    public void setSkuIdList(String skuIdList) {
        this.skuIdList = skuIdList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "SkuOrderInfoRespDTO{" +
                "Id=" + Id +
                ", skuIdList='" + skuIdList + '\'' +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", status=" + status +
                ", extra='" + extra + '\'' +
                '}';
    }
}
