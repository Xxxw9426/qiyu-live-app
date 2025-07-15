package org.qiyu.live.web.starter.error;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 自定义异常类
 * @Version: 1.0
 */

public class QiyuErrorException extends RuntimeException {

    private int errorCode;

    private String errorMsg;

    public QiyuErrorException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
