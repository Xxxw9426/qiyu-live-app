package org.qiyu.live.web.starter.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.qiyu.live.common.interfaces.enums.GatewayHeaderEnum;
import org.qiyu.live.web.starter.constants.RequestConstants;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-16
 * @Description: 用户信息拦截器，这个拦截器将在业务方法之前进行拦截获取用户信息
 * @Version: 1.0
 */
public class QiyuUserInfoInterceptor implements HandlerInterceptor {


    /***
     * 所有请求在被业务方法处理前都会进入该拦截器的业务方法前拦截
     * 在这个业务方法中会判断网关层是否给我们传了userId的参数，如果传了的话则会将其拿出来放入本地线程变量
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader(GatewayHeaderEnum.USER_LOGIN_ID.getName());
        // 如果userIdStr为空说明当前请求是白名单中的请求
        if(StringUtils.isEmpty(userIdStr)){
            // 不拦截
            return true;
        }
        // 参数不为空的话判断userId是否为空
        // userId不为空的话将其放入线程本地变量
        QiyuRequestContext.set(RequestConstants.QIYU_USER_ID,Long.valueOf(userIdStr));
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        QiyuRequestContext.clear();
    }
}
