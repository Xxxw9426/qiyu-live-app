package org.qiyu.live.im.core.server.common;

import org.qiyu.live.im.constants.ImConstants;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-25
 * @Description: 使用netty发送和接收消息的消息体实体类
 * @Version: 1.0
 */

public class ImMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = -6567417873780541989L;

    // 用于基本校验
    // 校验接收到的msg的magic与我们的预期是否相同的字段
    private short magic;

    // 用于标识当前消息的作用，后续会交给不同的handler处理
    private int code;

    // 用于记录消息体的长度
    private int len;

    // 存储消息体
    private byte[] body;

    public static ImMsg build(int code,String data) {
        ImMsg msg = new ImMsg();
        msg.setMagic(ImConstants.DEFAULT_MAGIC);
        msg.setCode(code);
        msg.setBody(data.getBytes());
        msg.setLen(msg.getBody().length);
        return msg;
    }

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ImMsg{" +
                "magic=" + magic +
                ", len=" + len +
                ", code=" + code +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
