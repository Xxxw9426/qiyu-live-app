package org.qiyu.live.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;

import java.io.File;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-04-24
 * @Description:
 * @Version: 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiWebApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ApiWebApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.SERVLET);
//        // 添加自定义的ApplicationContextInitializer
//        springApplication.addInitializers(context -> {
//            // 获取Environment对象
//            Environment env = context.getEnvironment();
//            // 从Environment中读取"spring.application.name"属性值
//            String appName = env.getProperty("spring.application.name");
//            String filePath = System.getProperty("user.home") + File.separator + ".dubbo" +File.separator + appName;
//            // 修改dubbo的本地缓存路径，避免缓存冲突
//            System.setProperty("dubbo.meta.cache.filePath", filePath);
//            System.setProperty("dubbo.mapping.cache.filePath",filePath);
//        });
        springApplication.run(args);
    }
}
