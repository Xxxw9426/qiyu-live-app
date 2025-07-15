package org.qiyu.live.bank.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 用户账户余额交易请求实体类
 * @Version: 1.0
 */

public class AccountTradeReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3852469488748386015L;

    // 请求用户
    private long userId;

    // 请求交易的金额
    private int num;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "AccountTradeReqDTO{" +
                "userId=" + userId +
                ", num=" + num +
                '}';
    }
}
