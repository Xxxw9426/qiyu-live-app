package org.qiyu.live.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.GiftRecordDTO;
import org.qiyu.live.gift.provider.dao.mapper.GiftRecordMapper;
import org.qiyu.live.gift.provider.dao.po.GiftRecordPO;
import org.qiyu.live.gift.provider.service.IGiftRecordService;
import org.springframework.stereotype.Service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-04
 * @Description: 直播间送礼服务送礼记录service接口实现类
 * @Version: 1.0
 */
@Service
public class GiftRecordServiceImpl implements IGiftRecordService {


    @Resource
    private GiftRecordMapper giftRecordMapper;


    /***
     * 插入一条送礼记录
     * @param giftRecordDTO
     */
    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        GiftRecordPO giftRecordPO = ConvertBeanUtils.convert(giftRecordDTO, GiftRecordPO.class);
        giftRecordMapper.insert(giftRecordPO);
    }
}
