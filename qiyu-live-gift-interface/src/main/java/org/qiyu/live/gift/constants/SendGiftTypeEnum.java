package org.qiyu.live.gift.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-09
 * @Description: 送礼种类枚举类
 * @Version: 1.0
 */

public enum SendGiftTypeEnum {

    DEFAULT_SEND_GIFT(0, "直播间默认送礼物"),
    PK_SEND_GIFT(1, "直播间PK送礼物");

    Integer code;
    String desc;

    SendGiftTypeEnum(Integer code, String desc) {
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
