package org.qiyu.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-27
 * @Description: 绑定用户id和其对应的channel的通用类
 * @Version: 1.0
 */

public class ChannelHandlerContextCache {


    /***
     * 当前的im服务启动的时候对外暴露的ip和端口
     */
    private static String SERVER_IP_ADDRESS="";


    // 存储用户id和用户channel的map
    private static Map<Long, ChannelHandlerContext> channelHandlerContextMap = new HashMap<>();


    /***
     * 根据用户id获取用户的channel
     * @param userId
     * @return
     */
    public static ChannelHandlerContext get(Long userId) {
        return channelHandlerContextMap.get(userId);
    }


    /***
     * 向map中存当前用户id和channel的方法
     * @param userId
     * @param channelHandlerContext
     */
    public static void put(Long userId, ChannelHandlerContext channelHandlerContext) {
        channelHandlerContextMap.put(userId, channelHandlerContext);
    }


    /***
     * 移除map中当前用户id对应的channel
     * @param userId
     */
    public static void remove(Long userId) {
        channelHandlerContextMap.remove(userId);
    }


    /***
     * 设置当前IM服务器启动的ip+端口
     * @param serverIpAddress
     */
    public static void setServerIpAddress(String serverIpAddress) {
        ChannelHandlerContextCache.SERVER_IP_ADDRESS = serverIpAddress;
    }


    /***
     * 获取当前IM服务器启动的ip+端口
     * @return
     */
    public static String getServerIpAddress() {
        return SERVER_IP_ADDRESS;
    }


}
