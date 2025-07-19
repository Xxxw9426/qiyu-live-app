package org.qiyu.live.gift.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-18
 * @Description: 订单状态枚举类
 * @Version: 1.0
 */
public enum SkuOrderInfoEnum {

    PREPARE_PAY(0, "待支付状态"),
    HAS_PAY(1, "已支付状态"),
    CANCEL(2, "取消订单状态");

    int code;
    String desc;

    SkuOrderInfoEnum(int code, String desc) {
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
