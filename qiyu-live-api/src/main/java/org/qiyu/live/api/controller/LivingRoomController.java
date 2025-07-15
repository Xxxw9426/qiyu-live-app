package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.error.ApiErrorEnum;
import org.qiyu.live.api.service.ILivingRoomService;
import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.req.OnlinePkReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.BizBaseErrorEnum;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.qiyu.live.web.starter.limit.RequestLimit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 接收前端有关开关播的请求的controller层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/living")
public class LivingRoomController {


    @Resource
    private ILivingRoomService livingRoomService;


    /***
     * 开播操作的接口
     * @param type
     * @return
     */
    @RequestLimit(limit = 1, second = 10, msg = "开播请求过于频繁，请稍后再试")
    @PostMapping("/startingLiving")
    public WebResponseVO startingLiving(Integer type) {
        // 调用rpc，往数据库中的开播表t_living_room写入一条记录
        ErrorAssert.isNotNull(type,BizBaseErrorEnum.PARAM_ERROR);
        Integer roomId = livingRoomService.startingLiving(type);
        LivingRoomInitVO initVO =new LivingRoomInitVO();
        initVO.setRoomId(roomId);
        return WebResponseVO.success(initVO);
    }


    /***
     * 关播操作的接口
     * @param roomId
     * @return
     */
    @RequestLimit(limit = 1, second = 10, msg = "关播请求过于频繁，请稍后再试")
    @PostMapping("/closeLiving")
    public WebResponseVO closeLiving(Integer roomId) {
        ErrorAssert.isNotNull(roomId,BizBaseErrorEnum.PARAM_ERROR);
        boolean closeStatus = livingRoomService.closeLiving(roomId);
        if(closeStatus) {
            return WebResponseVO.success();
        }
        return WebResponseVO.bizError("关播异常");
    }


    /***
     * 根据直播间id获取主播相关配置信息
     * @param roomId
     * @return
     */
    @PostMapping("/anchorConfig")
    public WebResponseVO anchorConfig(Integer roomId) {
        long userId= QiyuRequestContext.getUserId();
        return WebResponseVO.success(livingRoomService.anchorConfig(userId,roomId));
    }


    /***
     * 分页查询直播间列表
     * @param livingRoomReqVO
     * @return
     */
    @PostMapping("/list")
    public WebResponseVO list(LivingRoomReqVO livingRoomReqVO) {
        // 校验传入的参数
        ErrorAssert.isTrue(livingRoomReqVO==null || livingRoomReqVO.getType()==null, ApiErrorEnum.LIVING_ROOM_TYPE_MISSING);
        ErrorAssert.isTrue(livingRoomReqVO.getPage() >0 && livingRoomReqVO.getPageSize() <= 100, BizBaseErrorEnum.PARAM_ERROR);
        LivingRoomPageRespVO livingRoomPageRespVO = livingRoomService.list(livingRoomReqVO);
        return WebResponseVO.success(livingRoomPageRespVO);
    }


    /***
     * 用户请求连线pk的接口
     * @param onlinePkReqVO
     * @return
     */
    @PostMapping("/onlinePk")
    @RequestLimit(limit = 1, second = 3)
    public WebResponseVO onlinePk(OnlinePkReqVO onlinePkReqVO) {
        ErrorAssert.isNotNull(onlinePkReqVO.getRoomId(),BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.onlinePk(onlinePkReqVO));
    }


    /***
     * 主播请求开始初始化红包雨红包数据
     * @param livingRoomReqVO
     * @return
     */
    @RequestLimit(limit = 1, second = 10, msg = "正在初始化红包数据，请稍等")
    @PostMapping("/prepareRedPacket")
    public WebResponseVO prepareRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.prepareRedPacket(QiyuRequestContext.getUserId(), livingRoomReqVO.getRoomId()));
    }


    /***
     * 主播请求开始抢红包活动
     * @param livingRoomReqVO
     * @return
     */
    @RequestLimit(limit = 1, second = 10, msg = "正在广播直播间用户，请稍等")
    @PostMapping("/startRedPacket")
    public WebResponseVO startRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.startRedPacket(QiyuRequestContext.getUserId(), livingRoomReqVO.getRedPacketConfigCode()));
    }


    /***
     * 用户领取红包
     * @param livingRoomReqVO
     * @return
     */
    @RequestLimit(limit = 1,second = 7)
    @PostMapping("/getRedPacket")
    public WebResponseVO getRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.getRedPacket(QiyuRequestContext.getUserId(), livingRoomReqVO.getRedPacketConfigCode()));
    }
}
