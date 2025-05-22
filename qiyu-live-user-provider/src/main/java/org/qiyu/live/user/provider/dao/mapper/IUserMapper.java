package org.qiyu.live.user.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.user.provider.dao.po.UserPO;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-04-29
 * @Description: 用户信息数据库操作mapper接口
 * @Version: 1.0
 */

@Mapper
public interface IUserMapper extends BaseMapper<UserPO> {

}
