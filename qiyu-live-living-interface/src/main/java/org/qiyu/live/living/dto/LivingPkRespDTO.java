package org.qiyu.live.living.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-17
 * @Description: pk直播主播请求连线的响应实体类
 * @Version: 1.0
 */

public class LivingPkRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 240217807177482050L;

    private boolean onlineStatus;

    private String msg;

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "LivingPkRespDTO{" +
                "onlineStatus=" + onlineStatus +
                ", msg='" + msg + '\'' +
                '}';
    }
}
