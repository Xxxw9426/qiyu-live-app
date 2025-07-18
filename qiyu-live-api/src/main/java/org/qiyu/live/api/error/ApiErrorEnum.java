package org.qiyu.live.api.error;

import org.qiyu.live.web.starter.error.QiyuBaseError;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-05
 * @Description: 该API项目会用到的异常类
 * @Version: 1.0
 */

public enum ApiErrorEnum implements QiyuBaseError {

    LIVING_ROOM_TYPE_MISSING(10001,"需要给定直播间类型"),
    PHONE_NOT_BLANK(10002,"手机号不能为空"),
    PHONE_IN_VALID(10003,"手机号格式异常"),
    LOGIN_CODE_IN_VALID(10004,"验证码格式异常"),
    GIFT_CONFIG_ERROR(10005, "礼物信息异常"),
    SEND_GIFT_ERROR(10006,"送礼失败"),
    PK_ONLINE_BUSY(10007,"目前正有人连线，请稍后再试"),
    NOT_SEND_TO_YOURSELF(10008, "不能给自己送礼"),
    PRICE_NOT_ENOUGH(10009,"支付异常")
    ;

    int code;
    String desc;

    ApiErrorEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return desc;
    }
}
