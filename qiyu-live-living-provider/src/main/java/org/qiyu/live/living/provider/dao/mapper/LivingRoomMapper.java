package org.qiyu.live.living.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.qiyu.live.living.provider.dao.po.LivingRoomPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-02
 * @Description: 正在直播的直播间数据库表操作mapper接口
 * @Version: 1.0
 */
@Mapper
public interface LivingRoomMapper extends BaseMapper<LivingRoomPO> {
}
