package org.qiyu.live.user.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.user.provider.dao.po.UserPhonePO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-13
 * @Description: 操作用户电话相关服务数据库的mapper接口
 * @Version: 1.0
 */

@Mapper
public interface IUserPhoneMapper extends BaseMapper<UserPhonePO> {
}
