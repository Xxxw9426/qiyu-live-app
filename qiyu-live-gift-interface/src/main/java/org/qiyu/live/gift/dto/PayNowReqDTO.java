package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-19
 * @Description: 立即支付的订单的相关信息实体类
 * @Version: 1.0
 */
public class PayNowReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4714554906835449778L;

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
        return "PayNowReqDTO{" +
                "userId=" + userId +
                ", roomId=" + roomId +
                '}';
    }
}
