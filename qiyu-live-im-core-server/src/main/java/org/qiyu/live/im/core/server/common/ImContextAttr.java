package org.qiyu.live.im.core.server.common;

import io.netty.util.AttributeKey;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-27
 * @Description: 用于存储我们自定义的netty中给channel提供的Attr的属性内容
 * @Version: 1.0
 */

public class ImContextAttr {

    /***
     * 自定义：给我们的channel绑定名为"userId"的属性
     */
    public static AttributeKey<Long> USER_ID = AttributeKey.valueOf("userId");


    /***
     * 自定义：给我们的channel绑定名为"appId"的属性
     */
    public static AttributeKey<Integer> APP_ID = AttributeKey.valueOf("appId");


    /***
     * 自定义：给我们的channel绑定名为"roomId"的属性
     */
    public static AttributeKey<Integer> ROOM_ID = AttributeKey.valueOf("roomId");

}
