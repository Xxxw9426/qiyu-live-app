package org.qiyu.live.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.bank.provider.dao.po.PayProductPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-07
 * @Description: 付费产品数据库表操作mapper接口
 * @Version: 1.0
 */
@Mapper
public interface IPayProductMapper extends BaseMapper<PayProductPO> {
}
