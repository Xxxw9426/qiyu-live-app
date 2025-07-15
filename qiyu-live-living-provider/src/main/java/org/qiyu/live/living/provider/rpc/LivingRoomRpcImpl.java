package org.qiyu.live.living.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.living.dto.LivingPkRespDTO;
import org.qiyu.live.living.dto.LivingRoomReqDTO;
import org.qiyu.live.living.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.ILivingRoomRpc;
import org.qiyu.live.living.provider.service.ILivingRoomService;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 有关正在直播的直播间的相关操作的rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class LivingRoomRpcImpl implements ILivingRoomRpc {


    @Resource
    private ILivingRoomService livingRoomService;


    /**
     * 开启直播间的逻辑操作
     * @param livingRoomReqDTO
     * @return 返回开播的直播间的id
     */
    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.startLivingRoom(livingRoomReqDTO);
    }


    /**
     * 关闭直播间的逻辑操作
     * @param livingRoomReqDTO
     * @return
     */
    @Override
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.closeLiving(livingRoomReqDTO);
    }


    /**
     * 根据直播间id查询直播间相关信息
     * @param roomId
     * @return
     */
    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        return livingRoomService.queryByRoomId(roomId);
    }


    /***
     * 根据主播id查询直播间相关信息
     * @param anchorId
     * @return
     */
    @Override
    public LivingRoomRespDTO queryByAnchorId(Long anchorId) {
        return livingRoomService.queryByAnchorId(anchorId);
    }


    /***
     * 直播间列表的分页查询和展示
     * @param livingRoomReqDTO
     * @return
     */
    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.list(livingRoomReqDTO);
    }


    /***
     * 根据直播间的roomId和appId批量的查询当前直播间中的所有在线用户id集合
     * @param livingRoomReqDTO
     * @return
     */
    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.queryUserIdByRoomId(livingRoomReqDTO);
    }


    /***
     * 用户请求连线pk
     * @param livingRoomReqDTO
     * @return
     */
    @Override
    public LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.onlinePk(livingRoomReqDTO);
    }


    /***
     * 用户请求结束连接pk
     * @param livingRoomReqDTO
     * @return
     */
    @Override
    public boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.offlinePk(livingRoomReqDTO);
    }


    /***
     * 根据直播间id查询当前直播间中的pk者的id
     * @param roomId
     * @return
     */
    @Override
    public Long queryOnlinePkUserId(Integer roomId) {
        return livingRoomService.queryOnlinePkUserId(roomId);
    }
}
