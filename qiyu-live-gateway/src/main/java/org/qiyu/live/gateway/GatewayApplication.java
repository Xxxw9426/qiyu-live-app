package org.qiyu.live.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-09
 * @Description:
 * @Version: 1.0
 */

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(GatewayApplication.class);
        // 采用响应式的启动模式
        springApplication.setWebApplicationType(WebApplicationType.REACTIVE);
        springApplication.run(args);
    }
}
