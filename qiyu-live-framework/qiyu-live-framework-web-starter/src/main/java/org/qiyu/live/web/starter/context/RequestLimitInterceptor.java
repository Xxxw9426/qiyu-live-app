package org.qiyu.live.web.starter.context;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.qiyu.live.web.starter.error.QiyuErrorException;
import org.qiyu.live.web.starter.limit.RequestLimit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-05
 * @Description: 实现限流的拦截器Interceptor
 * @Version: 1.0
 */

public class RequestLimitInterceptor implements HandlerInterceptor {


    @Value("${spring.application.name}")
    private String applicationName;


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /***
     * 每次请求之前都会先经过这个方法
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 获取当前方法的参数并且判断当前方法的参数是否是以RequestLimit做的标识
        boolean hasLimit=handlerMethod.getMethod().isAnnotationPresent(RequestLimit.class);
        // 如果当前方法有限流注解
        if(hasLimit) {
            // 获取到限流注解
            RequestLimit requestLimit = handlerMethod.getMethod().getAnnotation(RequestLimit.class);
            Long userId = QiyuRequestContext.getUserId();
            if(userId==null) {
                return true;
            }
            // 做限流的控制
            // 做法：首先根据(userId + url + requestValue) 通过base64 -> 生成一个String(作为唯一的key)
            // 然后将其存入Redis中
            // 首次存入：redis -> key -> 将当前key对应的值set(1)
            // 后续再次存入：redis-> key -> 让当前key对应的值increment
            String cacheKey=applicationName+":"+userId +":"+ request.getRequestURI();
            int limit = requestLimit.limit();
            int second=requestLimit.second();
            Integer reqTime= (Integer) Optional.ofNullable(redisTemplate.opsForValue().get(cacheKey)).orElse(0);
            if(reqTime==0) {
                redisTemplate.opsForValue().set(cacheKey,1,second, TimeUnit.SECONDS);
                return true;
            } else if(reqTime<limit) {
                redisTemplate.opsForValue().increment(cacheKey,1);
                return true;
            }
            // 如果是已经超出了限流的范围：抛出限流异常
            throw new QiyuErrorException(-10001, requestLimit.msg());
        }
        return true;
    }
}
