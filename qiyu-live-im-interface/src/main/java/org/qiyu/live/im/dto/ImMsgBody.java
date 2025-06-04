package org.qiyu.live.im.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: netty服务接收和发送消息的消息体对象
 * @Version: 1.0
 */

public class ImMsgBody implements Serializable {

    @Serial
    private static final long serialVersionUID = -7657602083071950966L;

    // 接入Im服务的各个业务线id
    private int appId;

    // 用户id
    private long userId;

    // 唯一的消息id
    private String msgId;

    // 从业务服务中获取，用于在Im服务建立连接的时候使用
    private String token;

    // 业务标识，标明当前的业务data属于什么类型
    private int bizCode;

    // 和业务下游服务进行消息传递
    private String data;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    @Override
    public String toString() {
        return "ImMsgBody{" +
                "appId=" + appId +
                ", userId=" + userId +
                ", msgId='" + msgId + '\'' +
                ", token='" + token + '\'' +
                ", bizCode=" + bizCode +
                ", data='" + data + '\'' +
                '}';
    }
}
