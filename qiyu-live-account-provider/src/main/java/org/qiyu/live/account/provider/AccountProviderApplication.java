package org.qiyu.live.account.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.qiyu.live.account.provider.service.IAccountTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-15
 * @Description: token服务启动类
 * @Version: 1.0
 */

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class AccountProviderApplication implements CommandLineRunner {


    @Resource
    private IAccountTokenService accountTokenService;


    public static void main(String[] args) throws InterruptedException {
        //若Dubbo服务会自动关闭，加上CountDownLatch
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SpringApplication springApplication = new SpringApplication(AccountProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        countDownLatch.await();
    }


    // TODO 测试用户登录生成token以及token校验鉴权
    @Override
    public void run(String... args) throws Exception {
        /*Long userId=1092813L;
        String token = accountTokenService.createAndSaveLoginToken(userId);
        System.out.println("token is :"+token);
        Long userIdByToken = accountTokenService.getUserIdByToken(token);
        System.out.println("userIdByToken is :" + userIdByToken);*/
    }
}
