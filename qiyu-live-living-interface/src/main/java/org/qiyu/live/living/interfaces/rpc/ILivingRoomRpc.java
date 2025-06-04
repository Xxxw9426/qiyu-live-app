package org.qiyu.live.living.interfaces.rpc;

import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 有关正在直播的直播间的相关操作的rpc接口
 * @Version: 1.0
 */

public interface ILivingRoomRpc {


    /**
     * 开启直播间的逻辑操作
     * @param livingRoomReqDTO
     * @return 返回开播的直播间的id
     */
    Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO);


    /**
     * 关闭直播间的逻辑操作
     * @param livingRoomReqDTO
     * @return
     */
    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);


    /**
     * 根据直播间id查询直播间相关信息
     * @param roomId
     * @return
     */
    LivingRoomRespDTO queryByRoomId(Integer roomId);


    /***
     * 直播间列表的分页查询和展示
     * @param livingRoomReqDTO
     * @return
     */
    PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO);


    /***
     * 根据直播间的roomId和appId批量的查询当前直播间中的所有在线用户id集合
     * @param livingRoomReqDTO
     * @return
     */
    List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO);
}
