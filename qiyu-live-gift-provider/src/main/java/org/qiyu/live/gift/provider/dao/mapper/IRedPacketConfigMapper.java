package org.qiyu.live.gift.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.qiyu.live.gift.provider.dao.po.RedPacketConfigPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-18
 * @Description: 直播间红包雨服务红包雨配置数据库表操作mapper接口
 * @Version: 1.0
 */
@Mapper
public interface IRedPacketConfigMapper extends BaseMapper<RedPacketConfigPO> {


    /***
     * 更新数据库中本次红包活动中总共分发的红包金额
     * @param code
     * @param price
     */
    @Update("update qiyu_live_gift.t_red_packet_config set total_get_price=total_get_price+#{price} where config_code=#{code} and total_get_price>#{price}")
    void incrTotalGetPrice(@Param("code") String code, @Param("price") Integer price);


    /***
     * 更新数据库中本次红包活动中总共分发的红包数量
     * @param code
     */
    @Update("update qiyu_live_gift.t_red_packet_config set total_get=total_get+1 where config_code=#{code} ")
    void incrTotalGet(@Param("code") String code);
}
