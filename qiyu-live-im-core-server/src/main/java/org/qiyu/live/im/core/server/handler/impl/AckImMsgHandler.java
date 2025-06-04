package org.qiyu.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.qiyu.live.im.core.server.common.ImContextUtils;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-01
 * @Description: ack消息处理handler类
 * @Version: 1.0
 */
@Component
public class AckImMsgHandler implements SimpleHandler {


    private static final Logger LOGGER= LoggerFactory.getLogger(AckImMsgHandler.class);


    @Resource
    private IMsgAckCheckService msgAckCheckService;


    /***
     * 处理ack消息的逻辑
     * @param ctx
     * @param msg
     */
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg msg) {
        // 在接收到客户端传过来的ack消息后，删除缓存中的记录的对应消息的ack缓存
        Long userId= ImContextUtils.getUserId(ctx);
        Integer appId= ImContextUtils.getAppId(ctx);
        if(userId==null || appId==null){
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        // 调用service中的记录的方法向Redis记录
        msgAckCheckService.doMsgAck(JSON.parseObject(msg.getBody(), ImMsgBody.class));
    }
}
