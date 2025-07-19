package org.qiyu.live.gift.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-07-15
 * @Description: 商品sku信息数据库表相关操作mapper接口
 * @Version: 1.0
 */

@Mapper
public interface ISkuInfoMapper extends BaseMapper<SkuInfoPO> {
}
