package org.qiyu.live.web.starter.config;

import org.qiyu.live.web.starter.context.QiyuUserInfoInterceptor;
import org.qiyu.live.web.starter.context.RequestLimitInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/***
 * 拦截器配置类，配置本模块下的所有拦截器类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Bean
    public QiyuUserInfoInterceptor qiyuUserInfoInterceptor() {
        return new QiyuUserInfoInterceptor();
    }


    @Bean
    public RequestLimitInterceptor requestLimitInterceptor() {
        return new RequestLimitInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(qiyuUserInfoInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
        registry.addInterceptor(requestLimitInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
    }

}
