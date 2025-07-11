package org.qiyu.live.user.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.qiyu.live.user.provider.dao.po.UserTagPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-06
 * @Description: 用户标签数据库操作mapper接口
 * @Version: 1.0
 */

@Mapper
public interface IUserTagMapper extends BaseMapper<UserTagPO> {


    /***
     *  向数据库中设置用户标签
     *  使用或的思路来设置标签，只能允许第一次设置成功
     * @param userId
     * @param fieldName
     * @param tag
     * @return
     */
    @Update("update t_user_tag set ${fieldName}=${fieldName} | #{tag} where user_id=#{userId} and ${fieldName} & #{tag} = 0")
    int setTag(Long userId, String fieldName, long tag);


    /***
     *  取消数据库中的指定用户标签
     *  使用先取反在与的思路来取消标签，只能允许第一次删除成功
     * @param userId
     * @param fieldName
     * @param tag
     * @return
     */
    @Update("update t_user_tag set ${fieldName}=${fieldName} &~ #{tag} where user_id=#{userId} and ${fieldName} & #{tag} = #{tag}")
    int cancelTag(Long userId, String fieldName, long tag);
}
