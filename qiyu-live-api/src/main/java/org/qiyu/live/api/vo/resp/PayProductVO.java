package org.qiyu.live.api.vo.resp;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 付费产品页面展示数据响应实体类
 * @Version: 1.0
 */

public class PayProductVO {

    // 当前余额
    private Integer currentBalance;

    // 付费产品相关信息实体类集合
    private List<PayProductItemVO> payProductItems;

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    public List<PayProductItemVO> getPayProductItems() {
        return payProductItems;
    }

    public void setPayProductItems(List<PayProductItemVO> payProductItems) {
        this.payProductItems = payProductItems;
    }

    @Override
    public String toString() {
        return "PayProductVO{" +
                "currentBalance=" + currentBalance +
                ", payProductItems=" + payProductItems +
                '}';
    }
}
