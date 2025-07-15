package org.qiyu.live.web.starter.error;

import jakarta.servlet.http.HttpServletRequest;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 全局异常捕获器
 * @Version: 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /***
     * 监听java程序中的所有异常类型，返回WebResponseVO对象
     * @param request 监听的请求对象
     * @param e  要捕获的异常类型
     * @return
     */
    @ExceptionHandler(value = Exception.class)   // 指定想要监听的异常类型为java程序中的所有异常类型
    @ResponseBody
    public WebResponseVO errorHandle(HttpServletRequest request,Exception e) {
        // 打印我们监听到的出现异常的请求的url以及异常信息
        LOGGER.error(request.getRequestURI()+", error is ",e);
        return WebResponseVO.sysError("系统异常");
    }


    /***
     * 监听我们自定义的异常类，返回WebResponseVO对象
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = QiyuErrorException.class)   // 指定想要监听的异常类型为我们自定义的异常类
    @ResponseBody
    public WebResponseVO sysErrorHandle(HttpServletRequest request,QiyuErrorException e) {
        // 打印我们监听到的出现异常的请求的url以及异常信息
        LOGGER.error(request.getRequestURI()+", error is ",e);
        return WebResponseVO.bizError(e.getErrorMsg(),e.getErrorCode());
    }
}
