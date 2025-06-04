package org.qiyu.live.im.core.server.service.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
import org.qiyu.live.im.core.server.service.IRouterHandlerService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-29
 * @Description: Im服务对外暴露的专门给router层服务进行调用的rpc接口逻辑实现service接口
 * @Version: 1.0
 */
@Service
public class RouterHandlerServiceImpl implements IRouterHandlerService {


    @Resource
    private IMsgAckCheckService msgAckCheckService;


    /***
     * 根据消息接收者所在机器地址向消息接收者发送消息
     * @param imMsgBody
     */
    @Override
    public void onReceive(ImMsgBody imMsgBody) {
        // 发送消息并且成功后
        if(sendMsgToClient(imMsgBody)) {
            // 在向客户端推送了消息后往缓存中记录当前消息的ack记录
            msgAckCheckService.recordMsgAck(imMsgBody,1);
            // 接下来发送延迟消息来判断在延迟消息到达之前客户端是否已经收到了消息并且给我们发送了ack消息
            msgAckCheckService.sendDelayMsg(imMsgBody);
        }
    }


    /***
     * 发送消息给客户端
     * @param imMsgBody
     */
    @Override
    public boolean sendMsgToClient(ImMsgBody imMsgBody) {
        // 拿到消息接收者的userId
        long userId = imMsgBody.getUserId();
        // 获取到消息接收者的channel
        ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);
        // 用户当前仍然保持连接状态
        if(ctx != null) {
            String msgId = UUID.randomUUID().toString();
            imMsgBody.setMsgId(msgId);
            // 进行消息的推送
            ImMsg respMsg=ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBody));
            ctx.writeAndFlush(respMsg);
            return true;
        }
        return false;
    }
}
