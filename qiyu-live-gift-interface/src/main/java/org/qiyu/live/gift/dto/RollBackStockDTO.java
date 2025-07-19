package org.qiyu.live.gift.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-18
 * @Description: 库存回滚MQ延迟消息消息体实体类
 * @Version: 1.0
 */
public class RollBackStockDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7069560546309805503L;

    // 订单id
    private Long orderId;

    // 用户id
    private Long userId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "RollBackStockDTO{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                '}';
    }
}
