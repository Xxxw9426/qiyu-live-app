package org.qiyu.live.web.starter.error;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 专门给自定义异常类注入errorCode和errorMsg的枚举类
 * @Version: 1.0
 */

public enum BizBaseErrorEnum implements QiyuBaseError{

    PARAM_ERROR(100001,"参数异常"),
    TOKEN_ERROR(100002,"用户token异常");

    private int errorCode;

    private String errorMsg;

    BizBaseErrorEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public int getErrorCode() {
        return 0;
    }

    @Override
    public String getErrorMsg() {
        return "";
    }
}
