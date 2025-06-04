package org.qiyu.live.im.core.server.service;

import org.qiyu.live.im.dto.ImMsgBody;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-01
 * @Description: 操作消息发送后的ack消息的service接口
 * @Version: 1.0
 */

public interface IMsgAckCheckService {


    /***
     * 用于当IM服务收到ACK消息后移除Redis中的ACK记录
     * @param imMsgBody
     */
    void doMsgAck(ImMsgBody imMsgBody);


    /**
     * 用于在IM服务转发了消息给客户端后向Redis中记录消息的ack和times
     * @param imMsgBody
     * @param times
     */
    void recordMsgAck(ImMsgBody imMsgBody,int times);


    /***
     * 发送延迟消息，用于进行消息重试功能
     * @param imMsgBody
     */
    void sendDelayMsg(ImMsgBody imMsgBody);


    /***
     * 获取ack消息的重试次数
     * @param msgId
     * @param userId
     * @param appId
     * @return
     */
    int getMsgAckTimes(String msgId,long userId,int appId);
}
