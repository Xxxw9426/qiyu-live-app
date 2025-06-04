package org.qiyu.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.handler.ImHandlerFactory;
import org.qiyu.live.im.core.server.handler.SimpleHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: handler工厂实现类：根据传入消息的类型给出不同的handler做消息的处理和分发
 * @Version: 1.0
 */

@Component
public class ImHandlerFactoryImpl implements ImHandlerFactory, InitializingBean {


    // 定义一个map用来存放不同的handler和其对应的标识
    private static Map<Integer, SimpleHandler> simpleHandlerMap = new HashMap<>();


    @Resource
    private ApplicationContext applicationContext;


    /***
     * 根据传入消息的类型给出不同的handler做消息的处理和分发
     * @param ctx
     * @param msg
     */
    @Override
    public void doMsgHandler(ChannelHandlerContext ctx, ImMsg msg) {
        // 根据传过来的消息的code匹配其对应的处理handler
        SimpleHandler simpleHandler = simpleHandlerMap.get(msg.getCode());
        // 如果没有匹配上的话抛出异常
        if(simpleHandler == null) {
            throw new IllegalArgumentException("msg code is error,code is :"+msg.getCode());
        }
        // 匹配成功传入参数进行处理
        simpleHandler.handler(ctx, msg);
    }


    /***
     * 在spring容器初始化后该方法会被自动调用用来初始化map
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        // TODO 初始化map
        // 登录消息包：登录token验证，channel 和 userId 关联
        simpleHandlerMap.put(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), applicationContext.getBean(LoginMsgHandler.class));
        // 登出消息包：正常断开im连接时发送的
        simpleHandlerMap.put(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), applicationContext.getBean(LogoutMsgHandler.class));
        // 业务消息包：最常用的消息类型，例如我们的im收发数据
        simpleHandlerMap.put(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), applicationContext.getBean(BizImMsgHandler.class));
        // 心跳消息包：定时给im发送心跳包
        simpleHandlerMap.put(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), applicationContext.getBean(HeartBeatImMsgHandler.class));
        // ack消息包：处理消息接收方发送回来的ack消息
        simpleHandlerMap.put(ImMsgCodeEnum.IM_ACK_MSG.getCode(), applicationContext.getBean(AckImMsgHandler.class));
    }


}
