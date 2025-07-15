package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-14
 * @Description: 领取红包操作响应实体类
 * @Version: 1.0
 */

public class RedPacketReceiveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9059575343595910533L;

    // 领取到的红包的价格
    private Integer price;

    // 提示信息
    private String notifyMsg;

    public RedPacketReceiveDTO(Integer price) {
        this.price = price;
    }

    public RedPacketReceiveDTO(Integer price, String notifyMsg) {
        this.price = price;
        this.notifyMsg = notifyMsg;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getNotifyMsg() {
        return notifyMsg;
    }

    public void setNotifyMsg(String notifyMsg) {
        this.notifyMsg = notifyMsg;
    }

    @Override
    public String toString() {
        return "RedPacketReceiveDTO{" +
                "price=" + price +
                ", notifyMsg='" + notifyMsg + '\'' +
                '}';
    }
}
