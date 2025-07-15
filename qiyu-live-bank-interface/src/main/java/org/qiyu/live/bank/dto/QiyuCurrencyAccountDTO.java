package org.qiyu.live.bank.dto;

import java.util.Date;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 账户余额相关数据实体类
 * @Version: 1.0
 */

public class QiyuCurrencyAccountDTO {

    private Long userId;

    // 当前余额
    private int currentBalance;

    // 累计充值
    private int totalCharged;

    // 账户状态
    private Integer status;

    private Date createTime;

    private Date updateTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(int currentBalance) {
        this.currentBalance = currentBalance;
    }

    public int getTotalCharged() {
        return totalCharged;
    }

    public void setTotalCharged(int totalCharged) {
        this.totalCharged = totalCharged;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
        return "QiyuCurrencyAccountDTO{" +
                "userId=" + userId +
                ", currentBalance=" + currentBalance +
                ", totalCharged=" + totalCharged +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
