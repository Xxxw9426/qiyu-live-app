package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.gift.dto.ShopCarReqDTO;
import org.qiyu.live.gift.dto.ShopCarRespDTO;
import org.qiyu.live.gift.interfaces.IShopCarRpc;
import org.qiyu.live.gift.provider.service.IShopCarService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-16
 * @Description: 购物车相关操作rpc接口实现类
 * @Version: 1.0
 */
@DubboService
public class ShopCarRpcImpl implements IShopCarRpc {


    @Resource
    private IShopCarService shopCarService;


    /***
     * 向购物车中添加商品
     * @param reqDTO
     * @return
     */
    @Override
    public Boolean addCar(ShopCarReqDTO reqDTO) {
        return shopCarService.addCar(reqDTO);
    }


    /***
     * 从购物车中移除商品
     * @param reqDTO
     * @return
     */
    @Override
    public Boolean removeFromCar(ShopCarReqDTO reqDTO) {
        return shopCarService.removeFromCar(reqDTO);
    }


    /***
     * 查询购物车中的商品清单
     * @param reqDTO
     * @return
     */
    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO reqDTO) {
        return shopCarService.getCarInfo(reqDTO);
    }


    /***
     * 清空购物车
     * @param reqDTO
     * @return
     */
    @Override
    public Boolean clearShopCar(ShopCarReqDTO reqDTO) {
        return shopCarService.clearShopCar(reqDTO);
    }
}
