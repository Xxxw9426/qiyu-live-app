package org.qiyu.live.im.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: 所有会接入Im服务的服务的appId枚举类
 * @Version: 1.0
 */

public enum AppIdEnum {

    QIYU_LIVE_BIZ(10001,"旗鱼直播业务");

    int code;

    String desc;

    AppIdEnum(int code, String desc) {
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
