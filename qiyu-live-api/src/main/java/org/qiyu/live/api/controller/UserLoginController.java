package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.api.service.IUserLoginService;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.user.interfaces.IUserPhoneRPC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-14
 * @Description: 用户登录controller
 * @Version: 1.0
 */

@RestController
@RequestMapping("/userLogin")
public class UserLoginController {


    @Resource
    private IUserLoginService userLoginService;


    @DubboReference
    private IUserPhoneRPC userPhoneRPC;


    /***
     * 发送短信验证码
     * @param phone
     * @return
     */
    @PostMapping("/sendLoginCode")
    public WebResponseVO sendLoginCode(String phone) {
        return userLoginService.sendLoginCode(phone);
    }


    /***
     * 登录请求
     * 该方法中首先要校验验证码是否合法，然后判断用户是否注册过-> 初始化注册/老用户登录
     * @param phone
     * @param code
     * @param response
     * @return
     */
    @PostMapping("/login")
    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
        return userLoginService.login(phone,code,response);
    }
}
