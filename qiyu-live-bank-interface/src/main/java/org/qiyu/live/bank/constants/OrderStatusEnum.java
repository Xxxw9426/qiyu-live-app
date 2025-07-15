package org.qiyu.live.bank.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 订单状态枚举类
 * @Version: 1.0
 */

public enum OrderStatusEnum {

    WAITING_PAY(0, "待支付"),
    PAYING(1, "支付中"),
    PAYED(2, "已支付"),
    PAY_BACK(3, "撤销"),
    IN_VALID(4, "无效");

    int code;
    String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
