package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.LivingRoomInitVO;
import org.qiyu.live.api.vo.req.LivingRoomReqVO;
import org.qiyu.live.api.vo.req.OnlinePkReqVO;
import org.qiyu.live.api.vo.resp.LivingRoomPageRespVO;
import org.qiyu.live.api.vo.resp.RedPacketReceiveVO;

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


    /***
     * 用户请求连线pk
     * @param onlinePkReqVO
     * @return
     */
    boolean onlinePk(OnlinePkReqVO onlinePkReqVO);


    /***
     * 主播请求开始初始化红包雨红包数据
     * @param userId
     * @param roomId
     * @return
     */
    Boolean prepareRedPacket(Long userId, Integer roomId);


    /***
     * 主播请求开始抢红包活动
     * @param userId
     * @param code
     * @return
     */
    Boolean startRedPacket(Long userId, String code);


    /***
     * 用户领取红包
     * @param userId
     * @param redPacketConfigCode
     * @return
     */
    RedPacketReceiveVO getRedPacket(Long userId, String redPacketConfigCode);
}
