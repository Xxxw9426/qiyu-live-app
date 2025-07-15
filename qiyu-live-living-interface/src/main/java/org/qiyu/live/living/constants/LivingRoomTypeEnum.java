package org.qiyu.live.living.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 直播间种类枚举类
 * @Version: 1.0
 */

public enum LivingRoomTypeEnum {

    DEFAULT_LIVING_ROOM(1,"普通直播间"),

    PK_LIVING_ROOM(2,"pk直播间");

    Integer code;

    String desc;

    LivingRoomTypeEnum(int code, String desc) {
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
