package org.qiyu.live.api.service;

import jakarta.servlet.http.HttpServletResponse;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-14
 * @Description: 用户登录API层service接口
 * @Version: 1.0
 */

public interface IUserLoginService {


    /***
     * 发送短信验证码
     * @param phone
     * @return
     */
    WebResponseVO sendLoginCode(String phone);


    /***
     * 登录请求
     * 该方法中首先要校验验证码是否合法，然后判断用户是否注册过-> 初始化注册/老用户登录
     * @param phone
     * @param code
     * @param response
     * @return
     */
    WebResponseVO login(String phone, Integer code, HttpServletResponse response);
}
