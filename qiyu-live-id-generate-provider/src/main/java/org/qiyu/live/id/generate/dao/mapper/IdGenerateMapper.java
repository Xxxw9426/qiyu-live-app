package org.qiyu.live.id.generate.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.qiyu.live.id.generate.dao.po.IdGeneratePO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: id生成器数据库表mapper操作接口
 * @Version: 1.0
 */

@Mapper
public interface IdGenerateMapper extends BaseMapper<IdGeneratePO> {

    @Update("update qiyu_live_common.t_id_generate_config set next_threshold=next_threshold+step," +
            "current_start=current_start+step,version=version+1 where id =#{id} and version=#{version}")
    int updateNewIdCountAndVersion(@Param("id")int id, @Param("version")int version);

    @Select("select * from qiyu_live_common.t_id_generate_config")
    List<IdGeneratePO> selectAll();
}
