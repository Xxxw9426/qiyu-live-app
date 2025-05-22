package org.qiyu.live.user.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-13
 * @Description: 用户登录注册服务返回的结果的实体类
 * @Version: 1.0
 */

public class UserLoginDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = -4290788036479984698L;

    // 用户id
    private Long userId;

    // 是否登录成功
    private boolean isLoginSuccess;

    // 登录失败后返回提示信息
    private String desc;

    /***
     * 封装好的直接返回登录失败的方法
     * @param desc
     * @return
     */
    public static UserLoginDTO loginError(String desc) {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setLoginSuccess(false);
        loginDTO.setDesc(desc);
        return loginDTO;
    }

    /***
     * 封装好的直接返回userId和token的登录成功的方法
     * @param userId
     * @param token
     * @return
     */
    public static UserLoginDTO loginSuccess(Long userId, String token) {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setLoginSuccess(true);
        loginDTO.setUserId(userId);
        return loginDTO;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isLoginSuccess() {
        return isLoginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        isLoginSuccess = loginSuccess;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UserLoginDTO{" +
                "userId=" + userId +
                ", isLoginSuccess=" + isLoginSuccess +
                ", desc='" + desc + '\'' +
                '}';
    }
}
