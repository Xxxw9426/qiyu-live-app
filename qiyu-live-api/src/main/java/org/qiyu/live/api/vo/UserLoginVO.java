package org.qiyu.live.api.vo;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-14
 * @Description: 用户登录与前端交互的数据类
 * @Version: 1.0
 */

public class UserLoginVO {

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserLoginVO{" +
                "userId=" + userId +
                '}';
    }
}
