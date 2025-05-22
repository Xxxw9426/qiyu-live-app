package org.qiyu.live.msg.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.msg.provider.dao.po.SmsPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-12
 * @Description: 操作短信记录表的mapper接口
 * @Version: 1.0
 */

@Mapper
public interface SmsMapper extends BaseMapper<SmsPO> {
}
