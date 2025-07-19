package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 商品订单相关操作请求实体类
 * @Version: 1.0
 */
public class SkuOrderInfoReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4339322704298737807L;

    // 订单id
    private Long id;

    // 下单用户id
    private Long userId;

    // 用户下单的直播间
    private Integer roomId;

    // 订单状态
    private Integer status;

    // 用户下单的商品的集合
    private List<Long> skuIdList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Long> getSkuIdList() {
        return skuIdList;
    }

    public void setSkuIdList(List<Long> skuIdList) {
        this.skuIdList = skuIdList;
    }

    @Override
    public String toString() {
        return "SkuOrderInfoReqDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", status=" + status +
                ", skuIdList=" + skuIdList +
                '}';
    }
}
