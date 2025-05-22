package org.qiyu.live.common.interfaces.enums;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-15
 * @Description: 网关服务传递给下游的header枚举
 * @Version: 1.0
 */

public enum GatewayHeaderEnum {

    USER_LOGIN_ID("用户id","qiyu_gh_user_id");

    String desc;
    String name;

    GatewayHeaderEnum(String desc, String name) {
        this.desc = desc;
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }
}
