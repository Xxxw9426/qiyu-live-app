package org.qiyu.live.msg.enums;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-29
 * @Description: 进行消息转发的时候要转发到的下游业务的枚举类
 * @Version: 1.0
 */

public enum ImMsgBizCodeEnum {

    LIVING_ROOM_IM_CHAT_MSG_BIZ(5555, "直播间im聊天消息"),
    LIVING_ROOM_SEND_GIFT_SUCCESS(5556,"送礼成功"),
    LIVING_ROOM_SEND_GIFT_FAIL(5557,"送礼失败"),
    LIVING_ROOM_PK_SEND_GIFT_SUCCESS(5558,"pk送礼成功"),
    LIVING_ROOM_PK_ONLINE(5559,"pk连线"),
    START_RED_PACKET(5560,"开启红包雨活动");

    int code;
    String desc;

    ImMsgBizCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return desc;
    }
}
