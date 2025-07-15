package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.gift.dto.GiftRecordDTO;
import org.qiyu.live.gift.interfaces.IGiftRecordRpc;
import org.qiyu.live.gift.provider.service.IGiftRecordService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 直播间送礼服务送礼记录rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class GiftRecordRpcImpl implements IGiftRecordRpc {


    @Resource
    private IGiftRecordService giftRecordService;


    /***
     * 插入一条送礼记录
     * @param giftRecordDTO
     */
    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        giftRecordService.insertOne(giftRecordDTO);
    }
}
