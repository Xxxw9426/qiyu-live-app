package org.qiyu.live.user.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-07
 * @Description: 延迟双删中的统一删除类中的不同场景的code的枚举类
 * @Version: 1.0
 */

public enum CacheAsyncDeleteCode {

    USER_INFO_DELETE(0, "用户基础信息删除"),
    USER_TAG_DELETE(1, "用户标签删除");

    int code;      // 不同业务场景对应的code
    String desc;   // 对当前code的描述

    CacheAsyncDeleteCode(int code, String desc) {
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
