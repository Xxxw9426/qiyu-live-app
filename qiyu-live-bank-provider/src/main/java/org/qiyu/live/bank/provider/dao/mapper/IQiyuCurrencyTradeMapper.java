package org.qiyu.live.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.bank.provider.dao.po.QiyuCurrencyTradePO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-06
 * @Description: 流水记录数据库表操作mapper接口
 * @Version: 1.0
 */
@Mapper
public interface IQiyuCurrencyTradeMapper extends BaseMapper<QiyuCurrencyTradePO> {
}
