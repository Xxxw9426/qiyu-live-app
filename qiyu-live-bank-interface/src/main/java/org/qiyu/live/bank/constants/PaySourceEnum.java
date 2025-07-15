package org.qiyu.live.bank.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付来源枚举类
 * @Version: 1.0
 */

public enum PaySourceEnum {

    QIYU_LIVING_ROOM(1, "旗鱼直播间内支付"),
    QIYU_USER_CENTER(2, "用户中心内支付");

    private int code;
    private String desc;

    PaySourceEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /***
     * 根据传入的code判断当前支付的来源并返回
     * @param code
     * @return
     */
    public static PaySourceEnum find(int code) {
        for (PaySourceEnum value : PaySourceEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
