package org.qiyu.live.msg;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-29
 * @Description: 定义的向下游服务发送IM服务投递过来的消息的消息实体类
 * @Version: 1.0
 */


public class MessageDTO implements Serializable{

    @Serial
    private static final long serialVersionUID = 1226520299752658252L;

    // 消息发送者的userId
    private Long fromId;

    // 消息接收者的userId
    private Long toId;

    // 直播间roomId
    private Integer roomId;

    // 消息发送者的姓名
    private String senderName;

    // 消息发送者的头像
    private String senderAvtar;

    // 消息类型
    private Integer type;

    // 消息内容
    private String content;

    // 消息创建时间
    private Date createTime;

    // 消息更新时间
    private Date updateTime;

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvtar() {
        return senderAvtar;
    }

    public void setSenderAvtar(String senderAvtar) {
        this.senderAvtar = senderAvtar;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "fromId=" + fromId +
                ", toId=" + toId +
                ", roomId=" + roomId +
                ", senderName='" + senderName + '\'' +
                ", senderAvtar='" + senderAvtar + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
