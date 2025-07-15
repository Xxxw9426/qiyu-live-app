package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-18
 * @Description: 直播间红包雨服务相关操作service接口
 * @Version: 1.0
 */

public interface IRedPacketConfigService {


    /***
     * 根据主播id查询红包雨的相关配置数据
     * @param anchorId
     * @return
     */
    RedPacketConfigPO queryByAnchorId(Long anchorId);


    /***
     * 根据红包雨红包数据唯一code查询红包雨的相关配置数据
     * @param configCode
     * @return
     */
    RedPacketConfigPO queryByConfigCode(String configCode);


    /***
     * 新增一个红包雨服务信息
     * @param redPacketConfigPO
     * @return
     */
    boolean addOne(RedPacketConfigPO redPacketConfigPO);


    /***
     * 根据主键id修改红包雨相关配置
     * @param redPacketConfigPO
     * @return
     */
    boolean updateById(RedPacketConfigPO redPacketConfigPO);


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


    /**
     * 监听到用户领取红包的mq消息后的相关逻辑处理的方法
     * @param reqDTO
     * @param price
     */
    void receiveRedPacketHandle(RedPacketConfigReqDTO reqDTO,Integer price);

}
