package org.qiyu.live.gift.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 红包雨红包配置状态枚举类
 * @Version: 1.0
 */

public enum RedPacketStatusEnum {

    WAIT(1,"待准备"),
    IS_PREPARED(2, "已准备"),
    IS_SEND(3, "已发送");

    int code;
    String desc;

    RedPacketStatusEnum(int code, String desc) {
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
