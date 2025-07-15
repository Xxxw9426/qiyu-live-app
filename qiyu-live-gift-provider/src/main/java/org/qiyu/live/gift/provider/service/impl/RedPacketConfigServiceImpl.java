package org.qiyu.live.gift.provider.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.common.interfaces.utils.ListUtils;
import org.qiyu.live.gift.bo.SendRedPacketBO;
import org.qiyu.live.gift.constants.RedPacketStatusEnum;
import org.qiyu.live.gift.dto.RedPacketConfigReqDTO;
import org.qiyu.live.gift.dto.RedPacketReceiveDTO;
import org.qiyu.live.gift.provider.dao.mapper.IRedPacketConfigMapper;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;
import org.qiyu.live.gift.provider.service.IRedPacketConfigService;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.constants.ImMsgBizCodeEnum;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.qiyu.live.living.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.ILivingRoomRpc;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-18
 * @Description: 直播间红包雨服务相关操作service接口实现类
 * @Version: 1.0
 */
@Service
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {


    @DubboReference
    private ImRouterRpc routerRpc;


    @Resource
    private MQProducer mqProducer;


    @DubboReference
    private IQiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;


    @DubboReference
    private ILivingRoomRpc livingRoomRpc;


    @Resource
    private IRedPacketConfigMapper redPacketConfigMapper;


    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    /***
     * 根据主播id查询红包雨的相关配置数据
     * @param anchorId
     * @return
     */
    @Override
    public RedPacketConfigPO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getAnchorId, anchorId);
        queryWrapper.ne(RedPacketConfigPO::getStatus, RedPacketStatusEnum.IS_SEND.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }


    /***
     * 根据红包雨红包数据唯一code查询红包雨的相关配置数据
     * @param configCode
     * @return
     */
    @Override
    public RedPacketConfigPO queryByConfigCode(String configCode){
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getConfigCode, configCode);
        queryWrapper.eq(RedPacketConfigPO::getStatus, RedPacketStatusEnum.IS_PREPARED.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }


    /***
     * 新增一个红包雨服务信息
     * @param redPacketConfigPO
     * @return
     */
    @Override
    public boolean addOne(RedPacketConfigPO redPacketConfigPO) {
        redPacketConfigPO.setConfigCode(UUID.randomUUID().toString());
        return redPacketConfigMapper.insert(redPacketConfigPO) > 0;
    }


    /***
     * 根据主键id修改红包雨相关配置
     * @param redPacketConfigPO
     * @return
     */
    @Override
    public boolean updateById(RedPacketConfigPO redPacketConfigPO) {
        return redPacketConfigMapper.updateById(redPacketConfigPO)>0;
    }


    /***
     * 生成红包雨数据
     * @param anchorId
     * @return
     */
    @Override
    public boolean prepareRedPacket(Long anchorId) {
        // 首先根据主播id查询当前主播的红包数据
        RedPacketConfigPO configPO = this.queryByAnchorId(anchorId);
        // 重复生成或者错误参数传递的情况
        if(configPO==null) {
            return false;
        }
        // 为了防止多线程的情况下生成多个红包雨红包数据，我们需要进行加锁操作
        String code = configPO.getConfigCode();
        Boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKeyBuilder.buildRedPacketInitLock(code), 1, 3, TimeUnit.SECONDS);
        // 拿取锁失败
        if(!lockStatus) {
            return false;
        }
        // 要生成的红包总数量
        Integer totalCount = configPO.getTotalCount();
        // 要生成红包的总金额
        Integer totalPrice = configPO.getTotalPrice();
        // 生成红包数据
        List<Integer> priceList = this.createRedPacketPriceList(totalPrice,totalCount);
        // 将红包数据写入Redis
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        // 将大list集合进行拆分再写入Redis
        List<List<Integer>> splitPriceList = ListUtils.splitList(priceList, 100);
        // 遍历并且写入redis
        for (List<Integer> priceItemList : splitPriceList) {
            redisTemplate.opsForList().leftPush(cacheKey, priceItemList);
        }
        redisTemplate.expire(cacheKey,1,TimeUnit.DAYS);
        // 更新数据库中当前红包数据生成状态
        configPO.setStatus(RedPacketStatusEnum.IS_PREPARED.getCode());
        this.updateById(configPO);
        // 向Redis中写入当前红包雨红包数据已经初始化完成的标识
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildRedPacketPrepareSuccess(code),1,1,TimeUnit.DAYS);
        return true;
    }


    /***
     * 用户根据红包数据的唯一标识code领取红包的方法
     * @param reqDTO  红包数据的唯一标识code
     * @return
     */
    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO) {
        String code = reqDTO.getRedPacketConfigCode();
        // 从Redis中拿取到一个红包数据
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        Object obj = redisTemplate.opsForList().rightPop(cacheKey);
        if(obj==null) {
            return null;
        }
        Integer price=(Integer) obj;
        // 发送mq消息进行异步信息的统计，以及用户余额的增加
        SendRedPacketBO sendRedPacketBO = new SendRedPacketBO();
        sendRedPacketBO.setPrice(price);
        sendRedPacketBO.setReqDTO(reqDTO);
        Message message = new Message();
        // 设置发送的异步信息的主题为用户领取红包
        message.setTopic(GiftProviderTopicNames.RECEIVE_RED_PACKET);
        // 设置发送的异步信息的内容
        message.setBody(JSON.toJSONBytes(sendRedPacketBO));
        try {
            // 发送
            SendResult sendResult = mqProducer.send(message);
            // 发送成功
            if(SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                return new RedPacketReceiveDTO(price,"恭喜领取红包" + price + "旗鱼币");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 发送失败
        return new RedPacketReceiveDTO(null, "抱歉，红包被人抢走了，再试试？");
    }


    /***
     * 在缓存中生成红包雨红包金额数据的方法
     * @param totalPrice
     * @param totalCount
     * @return
     */
    private List<Integer> createRedPacketPriceList(Integer totalPrice, Integer totalCount) {
        List<Integer> redPacketPriceList = new ArrayList<>();
        // 生成红包数据，要求每个数据随机无序并且每个之间的偏差不是很大
        for(int i=0;i<totalCount;i++) {
            // 如果是最后一个红包
            if(i+1 ==totalCount) {
                redPacketPriceList.add(totalPrice);
                break;
            }
            int maxLimit=((totalPrice/(totalCount-i))*2);
            int currentPrice= ThreadLocalRandom.current().nextInt(1,maxLimit);
            totalPrice-=currentPrice;
            redPacketPriceList.add(currentPrice);
        }
        return redPacketPriceList;
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
        String code = reqDTO.getRedPacketConfigCode();
        // 首先判断红包数据是否已经初始化完成
        // 如果初始化未完成
        if(!redisTemplate.hasKey(cacheKeyBuilder.buildRedPacketPrepareSuccess(code))) {
            return false;
        }
        // 判断是否已经进行过了广播
        String notifyCacheKey = cacheKeyBuilder.buildRedPacketNotify(code);
        // 如果已经进行了广播
        if(redisTemplate.hasKey(notifyCacheKey)){
            return false;
        }
        // 如果没有进行广播的话
        // 广播通知所有用户
        // 获取当前红包数据
        RedPacketConfigPO configPO = this.queryByConfigCode(code);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("redPacketConfig", JSON.toJSONString(configPO));
        // 获取当前直播间中的所有在线用户集合
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(reqDTO.getRoomId());
        livingRoomReqDTO.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
        // 如果查询到的当前直播间的用户id集合为空
        if(CollectionUtils.isEmpty(userIdList)) {
            return false;
        }
        // 调用群发im消息的方法
        this.batchSendImMsg(userIdList,ImMsgBizCodeEnum.START_RED_PACKET,jsonObject);
        // 更改红包雨配置的状态为已发送
        configPO.setStatus(RedPacketStatusEnum.IS_SEND.getCode());
        // 标记广播的Redis缓存
        redisTemplate.opsForValue().set(notifyCacheKey,1,1,TimeUnit.DAYS);
        return true;
    }


    /***
     * 群发逻辑的发送IM消息
     * @param userIdList
     * @param bizCode
     * @param jsonObject
     */
    private void batchSendImMsg(List<Long> userIdList, ImMsgBizCodeEnum bizCode, JSONObject jsonObject) {
        // 为每一个用户创建自己的imMsgBody对象
        List<ImMsgBody> imMsgBodies=userIdList.stream().map(userId->{
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
            imMsgBody.setBizCode(bizCode.getCode());
            imMsgBody.setUserId(userId);
            imMsgBody.setData(jsonObject.toJSONString());
            return imMsgBody;
        }).collect(Collectors.toList());
        // rpc调用router层批量发送im消息的方法实现群发
        routerRpc.batchSendMsg(imMsgBodies);
    }


    @Override
    public void receiveRedPacketHandle(RedPacketConfigReqDTO reqDTO,Integer price) {
        String code = reqDTO.getRedPacketConfigCode();
        //  向Redis中记录关于红包分发的数据
        String totalGetCountCacheKey = cacheKeyBuilder.buildRedPacketTotalGetCount(code);
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalGetPrice(code);
        // 向Redis中记录当前用户在本次抢红包活动中总共领取到的红包金额
        redisTemplate.opsForValue().increment(cacheKeyBuilder.buildUserTotalGetPrice(reqDTO.getUserId()),price);
        // 向Redis中记录总共分发的红包数量
        redisTemplate.opsForValue().increment(totalGetCountCacheKey);
        // 过期时间为一天
        redisTemplate.expire(totalGetCountCacheKey,1,TimeUnit.DAYS);
        // 向Redis中记录总共分发的红包金额
        redisTemplate.opsForValue().increment(totalGetPriceCacheKey,price);
        // 过期时间为一天
        redisTemplate.expire(totalGetPriceCacheKey,1,TimeUnit.DAYS);
        // 设置当前红包的最大值：思路是首先我们首先从Redis中获取当前红包的最大值，然后判断当前的红包金额与最大红包值的大小
        // 但是由于这个操作需要执行两行代码，那么在多元化的远程调用的情况下，就有可能导致线程安全性问题，因此我们通常会使用luna脚本来实现。

        // 更新用户账户
        qiyuCurrencyAccountRpc.incr(reqDTO.getUserId(),price);
        // 更新数据库中的本次活动总共分发的金额
        redPacketConfigMapper.incrTotalGetPrice(code,price);
        // 更新数据库中本次红包活动中总共分发的红包数量
        redPacketConfigMapper.incrTotalGet(code);
    }
}
