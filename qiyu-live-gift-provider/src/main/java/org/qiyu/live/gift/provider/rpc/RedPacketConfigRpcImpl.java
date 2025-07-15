package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.RedPacketConfigRespDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.interfaces.IRedPacketConfigRpc;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;
import org.qiyu.live.gift.provider.service.IRedPacketConfigService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-18
 * @Description: 直播间红包雨服务相关操作rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class RedPacketConfigRpcImpl implements IRedPacketConfigRpc {


    @Resource
    private IRedPacketConfigService redPacketConfigService;


    /***
     * 根据主播id查询红包雨的相关配置数据
     * @param anchorId
     * @return
     */
    @Override
    public RedPacketConfigRespDTO queryByAnchorId(Long anchorId) {
        return ConvertBeanUtils.convert(redPacketConfigService.queryByAnchorId(anchorId),RedPacketConfigRespDTO.class);
    }


    /***
     * 新增一个红包雨服务信息
     * @param redPacketConfigReqDTO
     * @return
     */
    @Override
    public boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.addOne(ConvertBeanUtils.convert(redPacketConfigReqDTO, RedPacketConfigPO.class));
    }


    /***
     * 根据主键id修改红包雨相关配置
     * @param redPacketConfigReqDTO
     * @return
     */
    @Override
    public boolean updateById(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.updateById(ConvertBeanUtils.convert(redPacketConfigReqDTO, RedPacketConfigPO.class));
    }


    /***
     * 生成红包雨数据
     * @param anchorId
     * @return
     */
    @Override
    public boolean prepareRedPacket(Long anchorId) {
        return redPacketConfigService.prepareRedPacket(anchorId);
    }


    /***
     * 用户根据红包数据的唯一标识code领取红包的方法
     * @param reqDTO  红包数据的唯一标识code
     * @return
     */
    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.receiveRedPacket(reqDTO);
    }


    /***
     * 主播请求开始抢红包活动
     *   首先判断红包数据是否已经初始化完成
     *   然后通过im将红包雨的红包数据通知给直播间的所有用户
     * @param reqDTO
     * @return
     */
    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.startRedPacket(reqDTO);
    }
}
