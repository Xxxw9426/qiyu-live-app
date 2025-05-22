package org.qiyu.live.user.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.checkerframework.checker.units.qual.K;
import org.qiyu.live.user.constants.UserTagsEnum;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.dto.UserLoginDTO;
import org.qiyu.live.user.interfaces.IUserTagRpc;
import org.qiyu.live.user.provider.service.IUserPhoneService;
import org.qiyu.live.user.provider.service.IUserService;
import org.qiyu.live.user.provider.service.IUserTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-04-24
 * @Description: 用户服务中台provider启动类
 * @Version: 1.0
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class UserProviderApplication implements CommandLineRunner {


    @Resource
    private IUserTagService userTagService;


    @Resource
    private IUserService userService;


    @Resource
    private IUserPhoneService userPhoneService;


    private static final Logger LOGGER = LoggerFactory.getLogger(UserProviderApplication.class);


    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);   // 非web应用程序
        springApplication.run(args);
    }


    // TODO 测试用户登录注册发送短信验证码等功能
    @Override
    public void run(String... args) throws Exception {
       /* String phone="18789829049";
        UserLoginDTO userLoginDTO=userPhoneService.login(phone);
        System.out.println(userLoginDTO);
        System.out.println(userPhoneService.queryByUserId(userLoginDTO.getUserId()));
        System.out.println(userPhoneService.queryByUserId(userLoginDTO.getUserId()));
        System.out.println(userPhoneService.queryByPhone(phone));
        System.out.println(userPhoneService.queryByPhone(phone));*/
    }


    // TODO 测试用户服务中的延迟双删
    /*@Override
    public void run(String... args) throws Exception {
        // 用户基础信息的延迟双删
        Long userId=1004L;
        UserDTO userDTO=userService.getByUserId(userId);
        userDTO.setNickName("test-nick-name");
        userService.updateUserInfo(userDTO);

        // 用户标签服务的延迟双删
        System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
        System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_OLD_USER));
        System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
        System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_OLD_USER));
        System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
    }*/


    // TODO 测试用户标签的优化(分布式高并发场景下)
    /*@Override
    public void run(String... args) throws Exception {
        Long userId=1004L;
        CountDownLatch count=new CountDownLatch(1);
        for(int i=0;i<100;i++) {
            Thread t1=new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        count.await();
                        LOGGER.info("result is"+userTagService.setTag(userId,UserTagsEnum.IS_VIP));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            t1.start();
        }
        count.countDown();
        Thread.sleep(100000);
    }*/


    // TODO 初步测试用户标签中的设置标签
    /*@Override
    public void run(String... args) throws Exception {
        System.out.println(userTagService.setTag(1004L, UserTagsEnum.IS_VIP));
        System.out.println(userTagService.setTag(1004L, UserTagsEnum.IS_VIP));
        System.out.println("当前用户是否拥有 is_rich 标签："+userTagService.containTag(1001L, UserTagsEnum.IS_RICH));
        System.out.println("当前用户是否拥有 is_vip 标签："+userTagService.containTag(1001L, UserTagsEnum.IS_VIP));
        System.out.println(userTagService.setTag(1001L, UserTagsEnum.IS_OLD_USER));
        System.out.println("当前用户是否拥有 is_old_user 标签："+userTagService.containTag(1001L, UserTagsEnum.IS_OLD_USER));
        System.out.println("==========================================================================");
        System.out.println("当前用户是否拥有 is_rich 标签："+userTagService.containTag(1001L, UserTagsEnum.IS_RICH));
        System.out.println(userTagService.cancelTag(1004L, UserTagsEnum.IS_VIP));
        System.out.println(userTagService.cancelTag(1004L, UserTagsEnum.IS_VIP));
        System.out.println("当前用户是否拥有 is_vip 标签："+userTagService.containTag(1001L, UserTagsEnum.IS_VIP));
        System.out.println(userTagService.cancelTag(1001L, UserTagsEnum.IS_OLD_USER));
        System.out.println("当前用户是否拥有 is_old_user 标签："+userTagService.containTag(1001L, UserTagsEnum.IS_OLD_USER));
    }*/




}
