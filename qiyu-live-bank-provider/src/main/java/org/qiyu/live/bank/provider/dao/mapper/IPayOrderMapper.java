package org.qiyu.live.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.bank.provider.dao.po.PayOrderPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付订单数据库表相关操作mapper接口
 * @Version: 1.0
 */
@Mapper
public interface IPayOrderMapper extends BaseMapper<PayOrderPO> {
}
