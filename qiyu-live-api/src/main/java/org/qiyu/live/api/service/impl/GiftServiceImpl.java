package org.qiyu.live.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.qiyu.live.api.error.ApiErrorEnum;
import org.qiyu.live.api.service.IGiftService;
import org.qiyu.live.api.vo.req.GiftReqVO;
import org.qiyu.live.api.vo.resp.GiftConfigVO;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.common.interfaces.dto.SendGiftMq;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.qiyu.live.gift.interfaces.IGiftConfigRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 对礼物相关操作的service接口实现类
 * @Version: 1.0
 */
@Service
public class GiftServiceImpl implements IGiftService {


    private static final Logger LOGGER = LoggerFactory.getLogger(GiftServiceImpl.class);


    @DubboReference
    private IGiftConfigRpc giftConfigRpc;


    @Resource
    private MQProducer mqProducer;


    // 构建本地缓存(类似于map集合)
    private Cache<Integer,GiftConfigDTO> giftConfigDTOCache= Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(90, TimeUnit.SECONDS).build();


    /***
     * 查询所有礼物的配置信息
     * @return
     */
    @Override
    public List<GiftConfigVO> listGift() {
        List<GiftConfigDTO> giftConfigDTOList = giftConfigRpc.queryGiftList();
        return ConvertBeanUtils.convertList(giftConfigDTOList, GiftConfigVO.class);
    }


    /***
     * 在直播间中发送礼物
     * @param giftReqVO
     * @return
     */
    @Override
    public boolean send(GiftReqVO giftReqVO) {
        // 首先对参数进行校验
        // 校验礼物id是否合理
        int giftId=giftReqVO.getGiftId();
        // 首先在本地缓存中进行查询，如果本地缓存中有对象的话直接根据本地缓存进行校验
        // 如果本地缓存中没有对象的话，调用rpc进行查询并且校验，然后将查询到的对象注入本地缓存
        GiftConfigDTO giftConfigDTO = giftConfigDTOCache.get(giftId, new Function<Integer, GiftConfigDTO>() {
            /***
             * 如果没有从本地缓存中查询到对象的话，会回调这个方法进行远程的rpc调用并且将查询到的对象注入本地缓存
             * @param integer the function argument
             * @return
             */
            @Override
            public GiftConfigDTO apply(Integer integer) {
                return giftConfigRpc.getByGiftId(giftId);
            }
        });
        ErrorAssert.isNotNull(giftConfigDTO, ApiErrorEnum.GIFT_CONFIG_ERROR);
        ErrorAssert.isTrue(!giftReqVO.getSenderUserId().equals(giftReqVO.getReceiverId()),ApiErrorEnum.NOT_SEND_TO_YOURSELF);
        AccountTradeReqDTO reqDTO=new AccountTradeReqDTO();
        reqDTO.setUserId(QiyuRequestContext.getUserId());
        reqDTO.setNum(giftConfigDTO.getPrice());
        // 避免高并发高请求场景带来的rpc调用的风险，我们采用mq进行异步调用来削峰填谷
        Message message=new Message();
        message.setTopic(GiftProviderTopicNames.SEND_GIFT);
        // 封装mq中要发送的信息体
        SendGiftMq sendGiftMq=new SendGiftMq();
        sendGiftMq.setUserId(QiyuRequestContext.getUserId());
        sendGiftMq.setGiftId(giftId);
        sendGiftMq.setRoomId(giftReqVO.getRoomId());
        sendGiftMq.setReceiverId(giftReqVO.getReceiverId());
        sendGiftMq.setPrice(giftConfigDTO.getPrice());
        sendGiftMq.setUrl(giftConfigDTO.getSvgaUrl());
        sendGiftMq.setType(giftReqVO.getType());
        // 设置一个唯一的标识UUID，避免mq中二次消息导致用户被二次扣费
        sendGiftMq.setUuid(UUID.randomUUID().toString());
        message.setBody(JSON.toJSONBytes(sendGiftMq));
        try {
            SendResult sendResult=mqProducer.send(message);
            LOGGER.info("[gift-send] send result is {}", sendResult);
        } catch (Exception e) {
            LOGGER.info("[gift-send] send result is error:", e);
        }
//        AccountTradeRespDTO respDTO = qiyuCurrencyAccountRpc.consumeForSendGift(reqDTO);
//        ErrorAssert.isTrue(respDTO!=null && respDTO.isSuccess(),ApiErrorEnum.SEND_GIFT_ERROR);
        return true;
    }
}
