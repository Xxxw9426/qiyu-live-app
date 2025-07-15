package org.qiyu.live.web.starter.error;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 断言组件
 *   用来帮我们简化我们程序中的if-else条件判断
 * @Version: 1.0
 */
public class ErrorAssert {


    /***
     * 判断参数不能为空，如果参数为空则抛出一个异常
     * @param obj
     * @param qiyuBaseError
     */
    public static void isNotNull(Object obj,QiyuBaseError qiyuBaseError) {
        // 如果传入校验的参数为空的话抛出我们自定义的异常QiyuErrorException
        if(obj==null){
            throw new QiyuErrorException(qiyuBaseError.getErrorCode(), qiyuBaseError.getErrorMsg());
        }
    }


    /***
     * 判断字符串不能为空，如果字符串为空的话则抛出一个异常
     * @param str
     * @param qiyuBaseError
     */
    public static void isNotBlank(String str,QiyuBaseError qiyuBaseError) {
        // 如果传入的字符串为空或者去掉所有空格后长度为0的话则抛出我们自定义的异常QiyuErrorException
        if(str==null || str.trim().length()==0){
            throw new QiyuErrorException(qiyuBaseError.getErrorCode(), qiyuBaseError.getErrorMsg());
        }
    }


    /***
     * 判断布尔值是否为true，如果布尔值为false的话则抛出一个异常
     * @param flag
     * @param qiyuBaseError
     */
    public static void isTrue(boolean flag,QiyuBaseError qiyuBaseError) {
        // 如果传入的布尔值为false的话则抛出我们自定义的异常QiyuErrorException
        if(!flag){
            throw new QiyuErrorException(qiyuBaseError.getErrorCode(), qiyuBaseError.getErrorMsg());
        }
    }
}
