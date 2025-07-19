package org.qiyu.live.gift.provider.service.bo;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-17
 * @Description: 扣减商品库存后的响应实体类
 * @Version: 1.0
 */

public class DecrStockNumBO {

    // 更新库存是否成功
    private boolean isSuccess;

    // 当前是否还有库存
    private boolean noStock;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isNoStock() {
        return noStock;
    }

    public void setNoStock(boolean noStock) {
        this.noStock = noStock;
    }
}
