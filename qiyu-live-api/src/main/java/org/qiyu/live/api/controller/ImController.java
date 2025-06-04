package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.ImService;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-03
 * @Description: 前端接入IM服务器相关请求的controller层
 * @Version: 1.0
 */

@RestController
@RequestMapping("/im")
public class ImController {


    @Resource
    ImService imService;


    /***
     * 获取前端接入IM服务器所需要的配置信息
     * - im服务器地址
     * - 当前用户的token
     * @return
     */
    @PostMapping("/getImConfig")
    public WebResponseVO getImConfig() {
        return WebResponseVO.success(imService.getImConfig());
    }
}
