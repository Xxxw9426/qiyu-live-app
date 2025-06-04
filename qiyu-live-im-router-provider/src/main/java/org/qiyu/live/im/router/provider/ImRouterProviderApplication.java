package org.qiyu.live.im.router.provider;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.provider.service.ImRouterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;


/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: 启动类
 * @Version: 1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableDubbo
public class ImRouterProviderApplication implements CommandLineRunner {


    @Resource
    private ImRouterService imRouterService;


    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SpringApplication springApplication = new SpringApplication(ImRouterProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        countDownLatch.await();
    }


    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < 1000; i++) {
            ImMsgBody imMsgBody=new ImMsgBody();
            imRouterService.sendMsg(imMsgBody);
            Thread.sleep(1000);
        }

    }
}
