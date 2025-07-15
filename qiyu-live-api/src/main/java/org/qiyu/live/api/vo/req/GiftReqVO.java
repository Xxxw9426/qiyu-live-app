package org.qiyu.live.api.vo.req;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 直播间送礼物的请求实体类
 * @Version: 1.0
 */

public class GiftReqVO {

    // 礼物id
    private int giftId;

    // 直播间id
    private Integer roomId;

    // 送礼人id
    private Long senderUserId;

    // 接收人id
    private Long receiverId;

    // 送礼直播间的种类
    private int type;

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "GiftReqVO{" +
                "giftId=" + giftId +
                ", roomId=" + roomId +
                ", senderUserId=" + senderUserId +
                ", receiverId=" + receiverId +
                ", type=" + type +
                '}';
    }
}
