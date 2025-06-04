package imClient.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.dto.ImMsgBody;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-22
 * @Description: 客户端的handler类
 * @Version: 1.0
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ImMsg imMsg = (ImMsg) msg;
        // 接收到了服务器转发过来的业务消息，向服务器返回确认收到的ack消息
        if (imMsg.getCode() == ImMsgCodeEnum.IM_BIZ_MSG.getCode()) {
            ImMsgBody imMsgBody = JSON.parseObject(new String(imMsg.getBody()), ImMsgBody.class);
            ImMsgBody ackBody = new ImMsgBody();
            ackBody.setUserId(imMsgBody.getUserId());
            ackBody.setAppId(imMsgBody.getAppId());
            ackBody.setMsgId(imMsgBody.getMsgId());
            ImMsg ackMsg = ImMsg.build(ImMsgCodeEnum.IM_ACK_MSG.getCode(), JSON.toJSONString(ackBody));
            ctx.writeAndFlush(ackMsg);
        }
        System.out.println("【服务端响应数据】 result is " + new String(imMsg.getBody()));
    }
}
