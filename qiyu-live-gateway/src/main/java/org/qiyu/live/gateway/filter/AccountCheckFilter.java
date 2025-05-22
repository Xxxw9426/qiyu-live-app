package org.qiyu.live.gateway.filter;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.account.interfaces.IAccountTokenRpc;
import org.qiyu.live.common.interfaces.enums.GatewayHeaderEnum;
import org.qiyu.live.gateway.properties.GatewayApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.netty.handler.codec.http.cookie.CookieHeaderNames.MAX_AGE;
import static org.springframework.web.cors.CorsConfiguration.ALL;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-16
 * @Description: 网关鉴权的过滤器
 *    我们自己编写的过滤器需要实现GlobalFilter来实现在网关中加入鉴权过滤，
 *    并且还要实现ordered接口，让我们的过滤器比其他过滤器执行的早一些
 * @Version: 1.0
 */
@Component
public class AccountCheckFilter implements GlobalFilter, Ordered {


    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCheckFilter.class);


    @DubboReference
    private IAccountTokenRpc accountTokenRpc;


    @Resource
    private GatewayApplicationProperties gatewayApplicationProperties;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // TODO 我们加入网关鉴权的流程
        //  获取请求的url，判断是否为空，如果为空的话则返回请求不通过
        //  根据请求的url判断当前请求的url是否存在于白名单中，存在的话则不对token进行校验
        //  如果不在白名单中，那么我们需要提取cookie，对cookie做基本的格式校验
        //  token获取到之后，调用rpc判断token是否合法，如果合法则把token换取到的userId传递给下游
        //  如果token不合法，则拦截请求，日志记录token失效
        // 获取请求的url，判断是否为空，如果为空的话则返回请求不通过
        ServerHttpRequest request = exchange.getRequest();
        String reqUrl=request.getURI().getPath();
        // 允许跨域请求（和api模块中地loginserviceimpl中地一样，设置了域名才加这个，我这里无法解决跨域就没有设置域名）
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        // headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://web.qiyu.live.com:5500");
        // 这里我们不设置域名，就设置为localhost
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://127.0.0.1:5500");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
        if(StringUtils.isEmpty(reqUrl)) {
            return Mono.empty();
        }
        // 根据请求的url判断当前请求的url是否存在于白名单中，存在的话则不对token进行校验
        // 从我们配置好的Nacos配置读取类中读取我们的白名单
        List<String> notCheckUrlList = gatewayApplicationProperties.getNotCheckUrlList();
        for (String notCheckUrl : notCheckUrlList) {
            // 当前请求的路径在我们的白名单中
            if(reqUrl.startsWith(notCheckUrl)) {
                LOGGER.info("请求没有进行token校验，直接传达给业务下游");
                return chain.filter(exchange);
            }
        }
        // 如果不在白名单中，那么我们需要提取cookie，对cookie做基本的格式校验
        List<HttpCookie> httpCookieList = request.getCookies().get("qytk");
        if(CollectionUtils.isEmpty(httpCookieList)) {
            LOGGER.error("请求没有检索到qytk的cookie，被拦截");
            return Mono.empty();
        }
        String qiyuTokenCookieValue = httpCookieList.get(0).getValue();
        if(StringUtils.isEmpty(qiyuTokenCookieValue) || StringUtils.isEmpty(qiyuTokenCookieValue.trim())) {
            LOGGER.error("请求的cookie中的qytk是空，被拦截");
            return Mono.empty();
        }
        // token获取到之后，调用rpc判断token是否合法，如果合法则把token换取到的userId传递给下游
        Long userId = accountTokenRpc.getUserIdByToken(qiyuTokenCookieValue);
        //  如果token不合法，则拦截请求，日志记录token失效
        if(userId == null) {
            LOGGER.error("请求的token失效了，被拦截");
            return Mono.empty();
        }
        // 接下来将userId传给下游:通过gateway中的header封装我们要传递的参数传递给springboot-web服务
        // 在springboot-web服务中通过自定义filter或者interceptor来接收header获取userId参数
        ServerHttpRequest.Builder builder=request.mutate();
        builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getName(), String.valueOf(userId));
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
