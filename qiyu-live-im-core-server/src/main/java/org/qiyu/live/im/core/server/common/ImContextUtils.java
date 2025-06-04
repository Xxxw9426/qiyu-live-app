package org.qiyu.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-27
 * @Description: 通过ChannelHandlerContext的attr方法给我们的channel绑定/获取属性
 * @Version: 1.0
 */

public class ImContextUtils {


    /***
     * 根据channel获取当前channel对应的userId
     * @param ctx
     * @return
     */
    public static Long getUserId(ChannelHandlerContext ctx) {
        return ctx.attr(ImContextAttr.USER_ID).get();
    }


    /***
     * 给当前传入的channel绑定userId
     * @param ctx
     * @param userId
     */
    public static void setUserId(ChannelHandlerContext ctx,Long userId) {
         ctx.attr(ImContextAttr.USER_ID).set(userId);
    }


    /***
     * 根据channel获取当前channel对应的appId
     * @param ctx
     * @return
     */
    public static Integer getAppId(ChannelHandlerContext ctx) {
        return ctx.attr(ImContextAttr.APP_ID).get();
    }


    /***
     * 给当前传入的channel绑定appId
     * @param ctx
     * @param appId
     */
    public static void setAppId(ChannelHandlerContext ctx,Integer appId) {
        ctx.attr(ImContextAttr.APP_ID).set(appId);
    }


    /***
     * 移除channel我们设置的上下文userId
     * @param ctx
     */
    public static void removeUserId(ChannelHandlerContext ctx) {
        ctx.attr(ImContextAttr.USER_ID).remove();
    }


    /***
     * 移除channel我们设置的上下文appId
     * @param ctx
     */
    public static void removeAppId(ChannelHandlerContext ctx) {
        ctx.attr(ImContextAttr.APP_ID).remove();
    }


    /***
     * 根据channel获取当前channel对应的roomId
     * @param ctx
     * @return
     */
    public static Integer getRoomId(ChannelHandlerContext ctx) {
        return ctx.attr(ImContextAttr.ROOM_ID).get();
    }


    /***
     * 给当前传入的channel绑定roomId
     * @param ctx
     * @param roomId
     */
    public static void setRoomId(ChannelHandlerContext ctx, Integer roomId) {
        ctx.attr(ImContextAttr.ROOM_ID).set(roomId);
    }


    /***
     * 移除channel我们设置的上下文roomId
     * @param ctx
     */
    public static void removeRoomId(ChannelHandlerContext ctx) {
        ctx.attr(ImContextAttr.ROOM_ID).remove();
    }

}
