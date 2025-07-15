package org.qiyu.live.bank.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 启动类
 * @Version: 1.0
 */

@SpringBootApplication
@EnableDiscoveryClient
public class BankApiApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(BankApiApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.SERVLET);
        springApplication.run(args);
    }
}
