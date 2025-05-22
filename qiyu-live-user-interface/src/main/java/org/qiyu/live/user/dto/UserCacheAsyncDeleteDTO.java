package org.qiyu.live.user.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-07
 * @Description: 基于rocketMQ实现延迟双删功能时封装的统一删除类
 * @Version: 1.0
 */

public class UserCacheAsyncDeleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2291922809338528918L;

    /**
     * 不同业务场景的code，区别不同业务场景下的延迟消息
     */
    private int code;

    /***
     * 当前延迟双删场景下需要用到的删除参数
     */
    private String json;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }


}
