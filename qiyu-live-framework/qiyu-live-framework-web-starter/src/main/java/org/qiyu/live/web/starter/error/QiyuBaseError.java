package org.qiyu.live.web.starter.error;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 专门给自定义异常类注入errorCode和errorMsg的接口
 * @Version: 1.0
 */

public interface QiyuBaseError {

    // 定义返回的错误码
    int getErrorCode();

    // 定义返回的错误提示语
    String getErrorMsg();
}
