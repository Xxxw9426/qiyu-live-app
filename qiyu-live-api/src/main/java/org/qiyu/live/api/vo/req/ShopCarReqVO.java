package org.qiyu.live.api.vo.req;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 购物车相关操作请求实体类
 * @Version: 1.0
 */
public class ShopCarReqVO {

    // skuId
    private Long skuId;

    // 直播间id
    private Integer roomId;

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
        return "ShopCarReqVO{" +
                "skuId=" + skuId +
                ", roomId=" + roomId +
                '}';
    }
}
