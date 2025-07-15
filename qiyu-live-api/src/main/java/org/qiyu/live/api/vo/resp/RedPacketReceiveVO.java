package org.qiyu.live.api.vo.resp;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 用户领取红包功能返回响应实体类
 * @Version: 1.0
 */

public class RedPacketReceiveVO {

    // 红包金额
    private Integer price;

    // 提示语
    private String msg;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RedPacketReceiveVO{" +
                "price=" + price +
                ", msg='" + msg + '\'' +
                '}';
    }
}
