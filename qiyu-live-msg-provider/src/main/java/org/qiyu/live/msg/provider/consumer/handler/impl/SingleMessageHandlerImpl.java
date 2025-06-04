package org.qiyu.live.msg.provider.consumer.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.qiyu.live.msg.MessageDTO;
import org.qiyu.live.msg.enums.ImMsgBizCodeEnum;
import org.qiyu.live.msg.provider.consumer.handler.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-29
 * @Description: 处理IM服务中BizHandler放入消息队列中的消息的handler接口实现类
 * @Version: 1.0
 */
@Component
public class SingleMessageHandlerImpl implements MessageHandler {


    @DubboReference
    private ImRouterRpc imRouterRpc;


    @DubboReference
    private ILivingRoomRpc livingRoomRpc;


    /***
     * 处理IM服务中BizHandler放入消息队列中的消息
     * @param imMsgBody
     */
    @Override
    public void onMsgReceive(ImMsgBody imMsgBody) {
        // 获取当前消息的业务标识
        int bizCode = imMsgBody.getBizCode();
        // todo 直播间im聊天消息
        //  一个人发送，n个人接收：根据直播间roomId调用rpc方法获取对应直播间内的所有在线用户id集合进行消息的发送
        if(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode() == bizCode) {
            MessageDTO messageDTO= JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
            Integer roomId = messageDTO.getRoomId();
            LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
            livingRoomReqDTO.setRoomId(roomId);
            livingRoomReqDTO.setAppId(imMsgBody.getAppId());
            // 通过rpc调用获取当前直播间中的所有在线用户id集合(除掉自己，不用给自己发)
            List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO).stream().filter(x->!x.equals(imMsgBody.getUserId())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(userIdList)) {
                return;
            }
            List<ImMsgBody> imMsgBodyList = new ArrayList<>();
            // 遍历直播间中的在线用户为每个用户创建要发送的消息的实体对象
            userIdList.forEach(userId->{
                // 封装要发送的消息对象
                ImMsgBody respMsg=new ImMsgBody();
                respMsg.setUserId(userId);
                respMsg.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                respMsg.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
                respMsg.setData(JSON.toJSONString(messageDTO));
                // 加入集合
                imMsgBodyList.add(respMsg);
            });
            // 通过rpc调用实现批量转发
            imRouterRpc.batchSendMsg(imMsgBodyList);
        }
    }
}
