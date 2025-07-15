package org.qiyu.live.bank.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 交易类型的枚举类
 * @Version: 1.0
 */

public enum TradeTypeEnum {

    SEND_GIFT_TRADE(0, "送礼物交易"),
    LIVING_RECHARGE(1, "直播间充值"),
    ;

    private Integer code;
    private String desc;

    TradeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
