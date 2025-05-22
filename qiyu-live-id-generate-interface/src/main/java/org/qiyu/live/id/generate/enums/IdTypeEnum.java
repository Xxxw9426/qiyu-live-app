package org.qiyu.live.id.generate.enums;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: ID类型枚举类
 * @Version: 1.0
 */

public enum IdTypeEnum {

    USER_ID(1,"用户id生成策略");

    int code;
    String desc;

    IdTypeEnum(int code, String desc) {
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
