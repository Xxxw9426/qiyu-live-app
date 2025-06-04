package org.qiyu.live.im.constants;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: Im服务中会用到常量值的类
 * @Version: 1.0
 */

public class ImConstants {


    /** 消息校验辅助字段，随机值，用于校验服务端接收到的消息是否是我们预期中可以处理的消息   */
    public static final short DEFAULT_MAGIC = 18673;


    /** 定义的默认客户端心跳包的请求发送间隔时间，30秒 */
    public static final int DEFAULT_HEART_BEAT_GAP = 30;
}
