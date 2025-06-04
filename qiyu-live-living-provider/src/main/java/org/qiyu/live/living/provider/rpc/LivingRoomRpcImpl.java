package org.qiyu.live.living.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
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
}
