package org.qiyu.live.api.vo.req;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-16
 * @Description: 用户请求连线pk的请求实体类
 * @Version: 1.0
 */

public class OnlinePkReqVO {

    private Integer roomId;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "OnlinePkReqVO{" +
                "roomId=" + roomId +
                '}';
    }
}
