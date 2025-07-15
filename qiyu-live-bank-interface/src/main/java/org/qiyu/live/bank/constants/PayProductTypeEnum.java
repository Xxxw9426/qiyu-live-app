package org.qiyu.live.bank.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 付费产品种类枚举类
 * @Version: 1.0
 */

public enum PayProductTypeEnum {

    QIYU_COIN(0, "直播间充值-旗鱼虚拟币产品");

    Integer code;
    String desc;

    PayProductTypeEnum(Integer code, String desc) {
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
