package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 购物车相关操作响应实体类
 * @Version: 1.0
 */
public class ShopCarRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1892639857261714249L;

    // 用户id
    private Long userId;

    // 直播间id
    private Integer roomId;

    // 用户在当前直播间的购物车中的商品列表
    private List<ShopCarItemRespDTO> skuCarItemRespDTOS;

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

    public List<ShopCarItemRespDTO> getSkuCarItemRespDTOS() {
        return skuCarItemRespDTOS;
    }

    public void setSkuCarItemRespDTOS(List<ShopCarItemRespDTO> skuCarItemRespDTOS) {
        this.skuCarItemRespDTOS = skuCarItemRespDTOS;
    }

    @Override
    public String toString() {
        return "ShopCarRespDTO{" +
                "userId=" + userId +
                ", roomId=" + roomId +
                ", skuCarItemRespDTOS=" + skuCarItemRespDTOS +
                '}';
    }
}
