package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.qiyu.live.im.constants.ImConstants;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.qiyu.live.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: 心跳消息处理handler类
 * @Version: 1.0
 */

@Component
public class HeartBeatImMsgHandler implements SimpleHandler {


    private static final Logger LOGGER= LoggerFactory.getLogger(HeartBeatImMsgHandler.class);


    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;


    /***
     * 处理心跳检验的逻辑
     * @param ctx
     * @param msg
     */
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg msg) {
        // todo 基本思路
        //  1. 心跳包基本校验
        //  2. 心跳包record记录(使用Redis存储心跳记录)
        // 存储思路：首先对于海量数据，我们可以通过将对userId做取模运算的结果与常量拼接作为key值来实现Redis模拟分库分表。
        // 然后在当前用户所在的模拟表中基于zset集合存储心跳记录，其存储内容形式为key(用户的userId)-score(用户的心跳的时间戳)。
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        // 校验userId和appId
        if (userId == null || appId == null) {
            LOGGER.error("attr error, imMsgBody is {}", new String(msg.getBody()));
            // 有可能是错误的消息包导致，直接放弃连接
            ctx.close();
            throw new IllegalArgumentException("attr error");
        }
        // 将心跳信息记录在Redis中
        String redisKey=cacheKeyBuilder.buildOnlineZsetKey(userId,appId);
        this.recordOnlineTime(userId,redisKey);
        // 记录完本次的心跳信息后再删掉30秒前的上一次的心跳信息
        this.removeExpireRecord(redisKey);
        // 设置心跳记录过期时间为5分钟
        redisTemplate.expire(redisKey,5, TimeUnit.MINUTES);
        // 用户发送心跳包后我们还有更新用户id与建立长连接的机器的地址的缓存过期时间
        stringRedisTemplate.expire(ImCoreServerConstants.IM_BIND_IP_KEY+appId+userId,
                ImConstants.DEFAULT_HEART_BEAT_GAP*2,TimeUnit.SECONDS);
        // 设置响应给客户端的消息
        ImMsgBody msgBody=new ImMsgBody();
        msgBody.setUserId(userId);
        msgBody.setAppId(appId);
        msgBody.setData("true");
        ImMsg imMsg=ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(msgBody));
        LOGGER.info("[HeartBeatImMsgHandler] heartbeat msg, userId is {}, appId is {}", userId, appId);
        ctx.writeAndFlush(imMsg);
    }


    /***
     * 向Redis中存入当前用户最近一次的的心跳消息记录
     * @param userId
     * @param redisKey
     */
    private void recordOnlineTime(Long userId, String redisKey) {
        redisTemplate.opsForZSet().add(redisKey,userId,System.currentTimeMillis());
    }


    /***
     * 将当前时间的30秒之前的心跳消息从Redis中移除
     * 这里我们并不是严谨的设置了30秒，而是设置了两次心跳包的发送时间间隔，避免网络原因导致的心跳包发送时间不稳定的误差
     * @param redisKey
     */
    private void removeExpireRecord(String redisKey) {
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - ImConstants.DEFAULT_HEART_BEAT_GAP * 1000 * 2);
    }

}
