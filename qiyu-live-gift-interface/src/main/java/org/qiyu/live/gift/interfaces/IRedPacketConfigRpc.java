package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.RedPacketConfigRespDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-18
 * @Description: 直播间红包雨服务相关操作rpc接口
 * @Version: 1.0
 */

public interface IRedPacketConfigRpc {


    /***
     * 根据主播id查询红包雨的相关配置数据
     * @param anchorId
     * @return
     */
    RedPacketConfigRespDTO queryByAnchorId(Long anchorId);


    /***
     * 新增一个红包雨服务信息
     * @param redPacketConfigReqDTO
     * @return
     */
    boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO);


    /***
     * 根据主键id修改红包雨相关配置
     * @param redPacketConfigReqDTO
     * @return
     */
    boolean updateById(RedPacketConfigReqDTO redPacketConfigReqDTO);


    /***
     * 生成红包雨数据
     * @param anchorId
     * @return
     */
    boolean prepareRedPacket(Long anchorId);


    /***
     * 用户根据红包数据的唯一标识code领取红包的方法
     * @param reqDTO  红包配置相关属性实体类，可以获取到红包配置的唯一code
     * @return
     */
    RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO);


    /***
     * 主播请求开始抢红包活动
     *   首先判断红包数据是否已经初始化完成
     *   然后通过im将红包雨的红包数据通知给直播间的所有用户
     * @param reqDTO
     * @return
     */
    Boolean startRedPacket(RedPacketConfigReqDTO reqDTO);
}
