package org.qiyu.live.user.interfaces;

import org.qiyu.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-04-24
 * @Description: 用户中台rpc接口
 * @Version: 1.0
 */

public interface IUserRpc {

    /***
     * 根据userId查询UserDTO对象
     * @param userId
     * @return
     */
    UserDTO getUserById(Long userId);


    /***
     * 更新用户资料
     * @param userDTO
     * @return
     */
    boolean updateUserInfo(UserDTO userDTO);


    /***
     * 插入用户信息
     * @param userDTO
     * @return
     */
    boolean insertOne(UserDTO userDTO);


    /***
     * 批量查询用户信息
     * @param userIdList
     * @return
     */
    Map<Long,UserDTO> batchQueryUserInfo(List<Long> userIdList);
}

