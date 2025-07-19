package org.qiyu.live.gift.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.qiyu.live.gift.provider.dao.po.SkuStockInfoPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: sku库存信息数据库表相关操作mapper接口
 * @Version: 1.0
 */
@Mapper
public interface ISkuStockInfoMapper extends BaseMapper<SkuStockInfoPO> {


    /***
     * 更新sku库存信息
     *  在更新sku库存的时候，有可能会出现多线程的并发问题导致扣减库存失败，解决的方法有：
     *    1. 加乐观锁     即加入一个version版本号字段，在每一次更新前都需要提前知道当前的version版本号
     *                    通常情况下会循环3-5次，然后进行更新，只有当循环读取到的version和我们要更新的version一致的时候才可以进行更新操作
     *    2. 加悲观锁     如行锁
     *    3. 加分布式锁   加分布式锁就不需要我们在db层做一些特殊的操作，只需要在应用层通过Redis加一些分布式锁
     * @param skuId
     * @param num
     * @return
     */
    @Update("update qiyu_live_gift.t_sku_stock_info set qiyu_live_gift.t_sku_stock_info.stock_num=qiyu_live_gift.t_sku_stock_info.stock_num-#{num} " +
            "where qiyu_live_gift.t_sku_stock_info.sku_id=#{skuId} and qiyu_live_gift.t_sku_stock_info.stock_num-#{num} > 0 " +
            "and qiyu_live_gift.t_sku_stock_info.version=#{version}")
    int decrStockNumBySkuId(@Param("skuId") Long skuId, @Param("num") Integer num,@Param("version") Integer version);
}
