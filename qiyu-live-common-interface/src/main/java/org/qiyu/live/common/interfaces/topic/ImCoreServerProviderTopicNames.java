package org.qiyu.live.common.interfaces.topic;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-28
 * @Description: MQ消息topic常量类
 * @Version: 1.0
 */

public class ImCoreServerProviderTopicNames {


    /**
     * 接收IM系统发送的业务消息
     */
    public static final String QIYU_LIVE_IM_BIZ_MSG_TOPIC="qiyu_live_im_biz_msg_topic";


    /**
     * IM系统ACK发送的消息
     */
    public static final String QIYU_LIVE_IM_ACK_MSG_TOPIC="qiyu_live_im_ack_msg_topic";


    /**
     * 用户初次登录im服务发送mq
     */
    public static final String IM_ONLINE_TOPIC = "im_online_topic";


    /**
     * 用户断开im服务发送mq
     */
    public static final String IM_OFFLINE_TOPIC = "im_offline_topic";
}
