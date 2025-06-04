package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;

/**
*@Author: 萱子王
*@CreateTime: 2025-06-02
*@Description: 有关正在直播的直播间的相关操作的service接口
*@Version: 1.0
*/

public interface ILivingRoomService {


    /***
     * 开播的逻辑操作
     * @param type
     * @return
     */
    Integer startingLiving(Integer type);


    /***
     * 关播的逻辑操作
     * @param roomId
     * @return
     */
    boolean closeLiving(Integer roomId);


    /***
     * 根据直播间id获取直播相关配置信息
     * @param roomId
     * @return
     */
    LivingRoomInitVO anchorConfig(Long userId, Integer roomId);


    /***
     * 直播间列表加载展示
     * @param livingRoomReqVO
     * @return
     */
    LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO);
}
