package org.qiyu.live.common.interfaces.dto;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 发送礼物MQ消息的消息体实体类
 * @Version: 1.0
 */

public class SendGiftMq {

    // 礼物发送者id
    private Long userId;

    // 礼物id
    private Integer giftId;

    // 礼物价格
    private Integer price;

    // 礼物接收者id
    private Long receiverId;

    // 直播间id
    private Integer roomId;

    // 唯一标识：防止mq中的二次消费导致的二次扣费
    private String uuid;

    private String url;

    // 送礼直播间的种类
    private Integer type;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SendGiftMq{" +
                "userId=" + userId +
                ", giftId=" + giftId +
                ", price=" + price +
                ", receiverId=" + receiverId +
                ", roomId=" + roomId +
                ", uuid='" + uuid + '\'' +
                ", url='" + url + '\'' +
                ", type=" + type +
                '}';
    }
}
