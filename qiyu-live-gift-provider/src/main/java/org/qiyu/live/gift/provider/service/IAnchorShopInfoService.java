package org.qiyu.live.gift.provider.service;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 主播带货权限配置信息相关业务逻辑操作service接口
 * @Version: 1.0
 */

public interface IAnchorShopInfoService {


    /***
     * 根据主播id查询当前主播的带货商品skuId的集合
     * @param anchorId
     * @return
     */
    List<Long> querySkuIdByAnchorId(Long anchorId);


    /***
     * 查询所有有效的主播id
     * @return
     */
    List<Long> queryAllValidAnchorId();
}
