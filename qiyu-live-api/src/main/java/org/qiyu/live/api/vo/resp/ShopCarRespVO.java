package org.qiyu.live.api.vo.resp;

import org.qiyu.live.gift.dto.ShopCarItemRespDTO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 购物车相关操作响应实体类
 * @Version: 1.0
 */
public class ShopCarRespVO {

    // 用户id
    private Long userId;

    // 直播间id
    private Integer roomId;

    // 用户所在直播间的购物车清单
    private List<ShopCarItemRespDTO> shopCarItemRespDTOS;

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

    public List<ShopCarItemRespDTO> getShopCarItemRespDTOS() {
        return shopCarItemRespDTOS;
    }

    public void setShopCarItemRespDTOS(List<ShopCarItemRespDTO> shopCarItemRespDTOS) {
        this.shopCarItemRespDTOS = shopCarItemRespDTOS;
    }

    @Override
    public String toString() {
        return "ShopCarRespVO{" +
                "userId=" + userId +
                ", roomId=" + roomId +
                ", shopCarItemRespDTOS=" + shopCarItemRespDTOS +
                '}';
    }
}
