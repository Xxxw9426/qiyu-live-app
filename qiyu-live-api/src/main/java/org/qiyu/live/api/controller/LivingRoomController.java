package org.qiyu.live.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.api.service.ILivingRoomService;
import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
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
    @PostMapping("/startingLiving")
    public WebResponseVO startingLiving(Integer type) {
        // 调用rpc，往数据库中的开播表t_living_room写入一条记录
        if (type == null) {
            return WebResponseVO.errorParam("需要给定直播间类型");
        }
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
    @PostMapping("/closeLiving")
    public WebResponseVO closeLiving(Integer roomId) {
        if(roomId==null) {
            return WebResponseVO.errorParam("需要给定直播间id");
        }
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
        if(livingRoomReqVO==null || livingRoomReqVO.getType()==null) {
            return WebResponseVO.errorParam("需要给定直播间id");
        }
        if(livingRoomReqVO.getPage() <=0 || livingRoomReqVO.getPageSize() >100) {
            return WebResponseVO.errorParam("分页查询参数错误");
        }
        LivingRoomPageRespVO livingRoomPageRespVO = livingRoomService.list(livingRoomReqVO);
        return WebResponseVO.success(livingRoomPageRespVO);
    }
}
