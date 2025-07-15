package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.IGiftService;
import org.qiyu.live.api.vo.req.GiftReqVO;
import org.qiyu.live.api.vo.resp.GiftConfigVO;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 直播送礼服务的controller层
 * @Version: 1.0
 */

@RestController
@RequestMapping("/gift")
public class GiftController {


    @Resource
    private IGiftService giftService;


    /***
     * 查询所有礼物的配置信息
     * @return
     */
    @PostMapping("/listGift")
    public WebResponseVO listGift() {
        List<GiftConfigVO> giftConfigVOS=giftService.listGift();
        return WebResponseVO.success(giftConfigVOS);
    }


    /***
     * 在直播间中发送礼物
     * @return
     */
    @PostMapping("/send")
    public WebResponseVO send(GiftReqVO giftReqVO) {
        return WebResponseVO.success(giftService.send(giftReqVO));
    }


}
