package org.qiyu.live.gift.provider.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiyu.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.bank.dto.AccountTradeReqDTO;
import org.qiyu.live.bank.dto.AccountTradeRespDTO;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.dto.SendGiftMq;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.gift.constants.SendGiftTypeEnum;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.constants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.qiyu.live.living.dto.LivingRoomReqDTO;
import org.qiyu.live.living.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.ILivingRoomRpc;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 直播间发送礼物MQ消息的消费类
 * @Version: 1.0
 */
@Configuration
public class SendGiftConsumer implements InitializingBean {


    private static final Logger LOGGER = LoggerFactory.getLogger(SendGiftConsumer.class);


    // pk进度中的最大值
    private static final Long PK_MAX_NUM = 100L;


    // pk进度中的最小值
    private static final Long PK_MIN_NUM = -100L;


    // pk进度条的初始值
    private static final Long PK_INIT_NUM = 50L;


    @DubboReference
    private ImRouterRpc routerRpc;


    @DubboReference
    private IQiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;


    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;


    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @DubboReference
    private ILivingRoomRpc livingRoomRpc;


    // lua脚本：判断当前Redis中是否存在pk进度条的缓存，不存在的话设置为初始值，存在的话判断是否在0-100的区间内，
    // 符合区间要求的话根据我们传入的不同礼物对pk进度条的影响值进行进度条不同幅度的变化
    private String LUA_SCRIPT =
            "if (redis.call('exists', KEYS[1])) == 1 then " +
                    " local currentNum=redis.call('get',KEYS[1]) " +
                    " if (tonumber(currentNum)<=tonumber(ARGV[2]) and tonumber(currentNum)>=tonumber(ARGV[3])) then " +
                    " return redis.call('incrby',KEYS[1],tonumber(ARGV[4])) " +
                    " else return currentNum end " +
                    "else " +
                    "redis.call('set', KEYS[1], tonumber(ARGV[1])) " +
                    "redis.call('EXPIRE', KEYS[1], 3600 * 12) " +
                    "return ARGV[1] end";


    @Override
    public void afterPropertiesSet() throws Exception { DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        //老版本中会开启，新版本的mq不需要使用到
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + SendGiftConsumer.class.getSimpleName());
        mqPushConsumer.setConsumeMessageBatchMaxSize(10);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听发送礼物的行为
        mqPushConsumer.subscribe(GiftProviderTopicNames.SEND_GIFT, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                SendGiftMq sendGiftMq=JSON.parseObject(new String(msg.getBody()), SendGiftMq.class);
                // 向Redis中记录当前这条消息的消费记录，防止二次消息
                String cacheKey = cacheKeyBuilder.buildGiftConsumeKey(sendGiftMq.getUuid());
                boolean lockStatus=redisTemplate.opsForValue().setIfAbsent(cacheKey,-1,5, TimeUnit.MINUTES);
                // 向Redis中记录失败，说明这条消息已经消费过了，跳过
                if(!lockStatus){
                    continue;
                }
                // 向Redis中记录成功，说明这条消息没有被消费过，消费这条消息
                // 调用之前账户服务中写好的送礼物的底层逻辑实现
                AccountTradeReqDTO tradeReqDTO = new AccountTradeReqDTO();
                tradeReqDTO.setUserId(sendGiftMq.getUserId());
                tradeReqDTO.setNum(sendGiftMq.getPrice());
                // 扣减用户账户余额
                AccountTradeRespDTO respDTO = qiyuCurrencyAccountRpc.consumeForSendGift(tradeReqDTO);
                // 封装要响应的信息
                JSONObject jsonObject=new JSONObject();
                Integer sendGiftType = sendGiftMq.getType();
                // 发送礼物成功并且扣减用户余额成功
                if(respDTO.isSuccess()) {
                    Long receiverId=sendGiftMq.getReceiverId();
                    // todo 当前送礼类型是默认直播间的送礼类型
                    if (SendGiftTypeEnum.DEFAULT_SEND_GIFT.getCode().equals(sendGiftType)) {
                        // 向直播间中的所有用户发送IM消息
                        // 礼物的svga的url
                        jsonObject.put("url",sendGiftMq.getUrl());
                        // 获取当前直播间的所有在线用户
                        LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
                        reqDTO.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                        reqDTO.setRoomId(sendGiftMq.getRoomId());
                        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(reqDTO);
                        // 群发给直播间中的所有在线用户
                        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS.getCode(), jsonObject);
                    // todo 当前送礼类型是pk直播间的送礼类型
                    } else if(SendGiftTypeEnum.PK_SEND_GIFT.getCode().equals(sendGiftType)) {
                        this.pkImMsgSend(jsonObject,sendGiftMq,receiverId);
                    }
                // 发送礼物失败或者扣减余额失败
                } else {
                    // 通过im服务将发送礼物失败的消息告知给用户
                    jsonObject.put("msg", respDTO.getMsg());
                    // 转发给发送礼物的用户
                    this.sendImMsgSingleton(sendGiftMq.getUserId(), ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode(), jsonObject);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        LOGGER.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }


    /***
     * 单发逻辑的发送IM消息
     * @param userId
     * @param bizCode
     * @param jsonObject
     */
    private void sendImMsgSingleton(Long userId,int bizCode,JSONObject jsonObject) {
        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
        imMsgBody.setBizCode(bizCode);
        imMsgBody.setUserId(userId);
        imMsgBody.setData(jsonObject.toJSONString());
        routerRpc.sendMsg(imMsgBody);
    }


    /***
     * 群发逻辑的发送IM消息
     * @param userIdList
     * @param bizCode
     * @param jsonObject
     */
    private void batchSendImMsg(List<Long> userIdList, int bizCode, JSONObject jsonObject) {
        // 为每一个用户创建自己的imMsgBody对象
        List<ImMsgBody> imMsgBodies=userIdList.stream().map(userId->{
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setUserId(userId);
            imMsgBody.setData(jsonObject.toJSONString());
            return imMsgBody;
        }).collect(Collectors.toList());
        // rpc调用router层批量发送im消息的方法实现群发
        routerRpc.batchSendMsg(imMsgBodies);
    }


    /***
     * 收到pk直播间送礼消息的处理逻辑方法
     * @param jsonObject
     * @param sendGiftMq
     * @param receiverId
     */
    private void pkImMsgSend(JSONObject jsonObject, SendGiftMq sendGiftMq, Long receiverId) {
        // 将每个礼物对进度条的影响标识为一个数字,假设进度条长度一共是1000，每个礼物对于进度条的影响就是一个数值（500（A）：500（B），550：450）
        // 直播pk进度值的记录：向Redis中设置当前直播间的pk进度，以roomId为维度，用string类型来存储进度，送礼给（A）incr，送礼给（B）decr
        // 当数值到达了临界值后pk就结束。
        Integer roomId=sendGiftMq.getRoomId();
        // 判断当前pk直播间是否结束
        String pkStatus = cacheKeyBuilder.buildLivingPkIsOver(roomId);
        // 如果存在这个缓存的话证明当前pk已经结束
        if (redisTemplate.hasKey(pkStatus)) {
            return;
        }
        // 通过rpc调用获取当前正在直播的两个用户的id
        // pk主播1
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
        // pk主播2
        Long pkObjId = livingRoomRpc.queryOnlinePkUserId(roomId);
        // 直播不存在或者已结束或者pk用户已下线
        if(pkObjId==null || livingRoomRespDTO==null || livingRoomRespDTO.getAnchorId()==null) {
            return;
        }
        Long pkUserId=livingRoomRespDTO.getAnchorId();
        Long pkNum=0L;
        // 初始化Lua脚本用于对Redis中的pk进度条进行操作
        String incrKey = cacheKeyBuilder.buildLivingPkKey(roomId);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(Long.class);
        Long sendGiftSeqNum = System.currentTimeMillis();
        // 收到礼物的主播是pk主播1
        if(pkUserId.equals(receiverId)) {
            // 获取当前礼物对pk进度条的影响增幅
            Integer moveStep = sendGiftMq.getPrice() / 10;
            // 通过Lua脚本对Redis中当前pk进度条进行操作
            pkNum = this.redisTemplate.execute(redisScript, Collections.singletonList(incrKey), PK_INIT_NUM, PK_MAX_NUM, PK_MIN_NUM, moveStep);
            // 判断Redis中进度条的值是否到达了临界值
            if(PK_MAX_NUM <= pkNum) {
                // 向Redis中设置当前直播间为已经结束状态
                this.redisTemplate.opsForValue().set(cacheKeyBuilder.buildLivingPkIsOver(roomId),-1);
                jsonObject.put("winnerId",pkUserId);
            }
        // 收到礼物的主播是pk主播2
        } else if(pkObjId.equals(receiverId)) {
            // 获取当前礼物对pk进度条的影响减幅
            Integer moveStep = sendGiftMq.getPrice() / 10 * -1;
            pkNum = this.redisTemplate.execute(redisScript, Collections.singletonList(incrKey), PK_INIT_NUM, PK_MAX_NUM, PK_MIN_NUM, moveStep);
            // 判断Redis中进度条的值是否到达了临界值
            if (PK_MIN_NUM >= pkNum) {
                // 向Redis设置中当前直播间为已经结束状态
                this.redisTemplate.opsForValue().set(cacheKeyBuilder.buildLivingPkIsOver(roomId), -1);
                jsonObject.put("winnerId", pkObjId);
            }
        }
        // 向全直播间的用户都发送IM消息
        // 当前礼物接收者的id
        jsonObject.put("receiverId", sendGiftMq.getReceiverId());
        // 当前pk的进度条
        jsonObject.put("pkNum",pkNum);
        // 当前消息的序列号
        jsonObject.put("sendGiftSeqNum",sendGiftSeqNum);
        // 礼物的svga的url
        jsonObject.put("url",sendGiftMq.getUrl());
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
        this.batchSendImMsg(userIdList,ImMsgBizCodeEnum.LIVING_ROOM_PK_SEND_GIFT_SUCCESS.getCode(), jsonObject);
    }
}
