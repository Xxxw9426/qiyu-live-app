package org.qiyu.live.im.core.server.interfaces.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-29
 * @Description: 专门用户存储userId与其绑定的服务器机器的地址的Redis的key值的类
 * @Version: 1.0
 */

public class ImCoreServerConstants {


    /***
     * 在Redis要存储的用户id和其建立长连接的机器的ip地址的key值
     */
    public static String IM_BIND_IP_KEY = "qiyu-live-im:bind:ip:";
}
