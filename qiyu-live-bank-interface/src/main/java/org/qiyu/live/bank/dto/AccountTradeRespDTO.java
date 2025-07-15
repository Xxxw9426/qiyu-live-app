package org.qiyu.live.bank.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 用户账户余额交易结果响应实体类
 * @Version: 1.0
 */

public class AccountTradeRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7538297720339034013L;

    // 请求用户
    private long userId;

    // 交易结果
    private boolean isSuccess;

    // 错误码
    private int code;

    // 交易结果提示信息
    private String msg;

    /***
     * 设置返回交易失败的提示信息的对象
     * @param userId
     * @param msg
     * @return
     */
    public static AccountTradeRespDTO buildFail(long userId, String msg, int code) {
        AccountTradeRespDTO dto = new AccountTradeRespDTO();
        dto.setCode(code);
        dto.setUserId(userId);
        dto.setSuccess(false);
        dto.setMsg(msg);
        return dto;
    }

    /***
     * 设置返回交易成功的对象
     * @param userId
     * @return
     */
    public static AccountTradeRespDTO buildSuccess(long userId, String msg) {
        AccountTradeRespDTO dto = new AccountTradeRespDTO();
        dto.setUserId(userId);
        dto.setMsg(msg);
        dto.setSuccess(true);
        return dto;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "AccountTradeRespDTO{" +
                "userId=" + userId +
                ", isSuccess=" + isSuccess +
                ", msg='" + msg + '\'' +
                '}';
    }
}
