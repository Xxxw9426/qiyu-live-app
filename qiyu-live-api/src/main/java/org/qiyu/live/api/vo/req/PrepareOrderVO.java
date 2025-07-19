package org.qiyu.live.api.vo.req;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-18
 * @Description: 商品待支付订单相关操作实体类
 * @Version: 1.0
 */
public class PrepareOrderVO {

    // 用户id
    private Long userId;

    // 直播间id
    private Integer roomId;

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

    @Override
    public String toString() {
        return "PrepareOrderVO{" +
                "userId=" + userId +
                ", roomId=" + roomId +
                '}';
    }
}
