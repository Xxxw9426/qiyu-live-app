package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.GiftConfigDTO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 直播间送礼服务礼物配置rpc接口
 * @Version: 1.0
 */

public interface IGiftConfigRpc {


    /***
     * 根据礼物id查询礼物的配置信息
     * @param giftId
     * @return
     */
    GiftConfigDTO getByGiftId(Integer giftId);


    /***
     * 查询所有礼物信息
     * @return
     */
    List<GiftConfigDTO> queryGiftList();


    /***
     * 插入一个礼物的信息
     * @param giftConfigDTO
     */
    void insertOne(GiftConfigDTO giftConfigDTO);


    /***
     * 更新一个礼物的信息
     * @param giftConfigDTO
     */
    void updateOne(GiftConfigDTO giftConfigDTO);

}
