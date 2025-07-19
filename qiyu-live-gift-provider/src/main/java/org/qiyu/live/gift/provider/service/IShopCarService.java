package org.qiyu.live.gift.provider.service;

import org.qiyu.live.gift.dto.ShopCarReqDTO;
import org.qiyu.live.gift.dto.ShopCarRespDTO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 购物车相关操作service接口
 * @Version: 1.0
 */
public interface IShopCarService {

    /***
     * 向购物车中添加商品
     * @param reqDTO
     * @return
     */
    Boolean addCar(ShopCarReqDTO reqDTO);


    /***
     * 从购物车中移除商品
     * @param reqDTO
     * @return
     */
    Boolean removeFromCar(ShopCarReqDTO reqDTO);


    /***
     * 查询购物车中的商品清单
     * @param reqDTO
     * @return
     */
    ShopCarRespDTO getCarInfo(ShopCarReqDTO reqDTO);


    /***
     * 清空购物车
     * @param reqDTO
     * @return
     */
    Boolean clearShopCar(ShopCarReqDTO reqDTO);


    /***
     * 增加购物车中某个商品的数量
     * @param reqDTO
     * @return
     */
    Boolean addCarItemNum(ShopCarReqDTO reqDTO);
}
