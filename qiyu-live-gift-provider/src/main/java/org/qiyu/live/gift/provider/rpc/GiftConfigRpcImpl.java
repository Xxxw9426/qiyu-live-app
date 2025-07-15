package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.qiyu.live.gift.interfaces.IGiftConfigRpc;
import org.qiyu.live.gift.provider.service.IGiftConfigService;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 直播间送礼服务礼物配置rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class GiftConfigRpcImpl implements IGiftConfigRpc {


    @Resource
    private IGiftConfigService giftConfigService;


    /***
     * 根据礼物id查询礼物的配置信息
     * @param giftId
     * @return
     */
    @Override
    public GiftConfigDTO getByGiftId(Integer giftId) {
        return giftConfigService.getByGiftId(giftId);
    }


    /***
     * 查询所有礼物信息
     * @return
     */
    @Override
    public List<GiftConfigDTO> queryGiftList() {
        return giftConfigService.queryGiftList();
    }


    /***
     * 插入一个礼物的信息
     * @param giftConfigDTO
     */
    @Override
    public void insertOne(GiftConfigDTO giftConfigDTO) {
        giftConfigService.insertOne(giftConfigDTO);
    }


    /***
     * 更新一个礼物的信息
     * @param giftConfigDTO
     */
    @Override
    public void updateOne(GiftConfigDTO giftConfigDTO) {
        giftConfigService.updateOne(giftConfigDTO);
    }

}
