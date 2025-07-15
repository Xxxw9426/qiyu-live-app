package org.qiyu.live.gift.bo;

import org.qiyu.live.gift.dto.RedPacketConfigReqDTO;
import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 用户领取红包后相关逻辑操作mq通知类
 * @Version: 1.0
 */

public class SendRedPacketBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2173511518005452481L;

    // 金额
    private Integer price;

    // 红包相关配置
    private RedPacketConfigReqDTO reqDTO;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public RedPacketConfigReqDTO getReqDTO() {
        return reqDTO;
    }

    public void setReqDTO(RedPacketConfigReqDTO reqDTO) {
        this.reqDTO = reqDTO;
    }

    @Override
    public String toString() {
        return "SendRedPacketBO{" +
                "price=" + price +
                ", reqDTO=" + reqDTO +
                '}';
    }
}
