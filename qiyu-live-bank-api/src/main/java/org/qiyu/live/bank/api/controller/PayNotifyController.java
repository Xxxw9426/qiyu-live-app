package org.qiyu.live.bank.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.bank.api.service.IPayNotifyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付回调请求接收controller
 * @Version: 1.0
 */
@RestController
@RequestMapping("/payNotify")
public class PayNotifyController {


    @Resource
    private IPayNotifyService payNotifyService;


    /***
     * 接收到微信的支付回调后的逻辑处理
     * @param param
     * @return 处理成功后返回success
     */
    @PostMapping("wx-notify")
    public String wxNotify(@RequestParam("param") String param) {
        return payNotifyService.notifyHandler(param);
    }
}
