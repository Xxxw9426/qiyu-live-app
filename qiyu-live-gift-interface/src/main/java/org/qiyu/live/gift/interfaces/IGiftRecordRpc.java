package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.GiftRecordDTO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 直播间送礼服务送礼记录rpc接口
 * @Version: 1.0
 */
public interface IGiftRecordRpc {


    /***
     * 插入一条送礼记录
     * @param giftRecordDTO
     */
    void insertOne(GiftRecordDTO giftRecordDTO);
}
