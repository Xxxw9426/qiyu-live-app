package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 购物车相关操作请求实体类
 * @Version: 1.0
 */
public class ShopCarReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4611385542916150690L;

    // 用户id
    private Long userId;

    // 商品id
    private Long skuId;

    // 直播间id
    private Integer roomId;

    public ShopCarReqDTO() {
    }

    public ShopCarReqDTO(Long userId, Long skuId, Integer roomId) {
        this.userId = userId;
        this.skuId = skuId;
        this.roomId = roomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "ShopCarReqDTO{" +
                "userId=" + userId +
                ", skuId=" + skuId +
                ", roomId=" + roomId +
                '}';
    }
}
