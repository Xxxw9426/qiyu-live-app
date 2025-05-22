package org.qiyu.live.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.qiyu.live.common.interfaces.enums.GatewayHeaderEnum;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-16
 * @Description: 前端页面初始化时调用的controller
 * @Version: 1.0
 */

@RestController
@RequestMapping("/home")
public class HomePageController {


    /***
     * 前端调用刷新页面时向后端发送的请求
     * 在这个方法中如果我们返回success状态说明当前请求刷新页面的用户已经登录过了并且token依旧有效
     * @return
     */
    @PostMapping("/initPage")
    public WebResponseVO initPage() {
        Long userId = QiyuRequestContext.getUserId();
        System.out.println(userId);
        return WebResponseVO.success();
    }

}
