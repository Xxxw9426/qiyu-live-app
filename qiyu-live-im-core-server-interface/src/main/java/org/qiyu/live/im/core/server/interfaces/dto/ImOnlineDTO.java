package org.qiyu.live.im.core.server.interfaces.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-03
 * @Description: IM服务器发送过来的用户连接IM服务器的数据实体类
 * @Version: 1.0
 */

public class ImOnlineDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7744125342169651908L;

    private Long userId;

    private Integer appId;

    private Integer roomId;

    private Long loginTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    @Override
    public String toString() {
        return "ImOnlineDTO{" +
                "userId=" + userId +
                ", appId=" + appId +
                ", roomId=" + roomId +
                ", loginTime=" + loginTime +
                '}';
    }
}
