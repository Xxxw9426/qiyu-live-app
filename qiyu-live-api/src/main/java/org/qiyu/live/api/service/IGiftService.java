package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.req.GiftReqVO;
import org.qiyu.live.api.vo.resp.GiftConfigVO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 对礼物相关操作的service接口
 * @Version: 1.0
 */

public interface IGiftService {


    /***
     * 查询所有礼物的配置信息
     * @return
     */
    List<GiftConfigVO> listGift();


    /***
     * 在直播间中发送礼物
     * @param giftReqVO
     * @return
     */
    boolean send(GiftReqVO giftReqVO);
}
