package org.qiyu.live.user.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.user.interfaces.IUserRpc;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.provider.service.IUserService;

import java.util.List;
import java.util.Map;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-04-24
 * @Description: 用户中台rpc接口实现类
 * @Version: 1.0
 */
// 如果我们要实现的是Dubbo服务需要加@DubboService注解
@DubboService
public class UserRpcImpl implements IUserRpc {

    @Resource
    private IUserService userService;

    /***
     * 根据userId查询UserDTO对象
     * @param userId
     * @return
     */
    @Override
    public UserDTO getByUserId(Long userId) {
        return userService.getByUserId(userId);
    }


    /***
     * 更新用户资料
     * @param userDTO
     * @return
     */
    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        return userService.updateUserInfo(userDTO);
    }


    /***
     * 插入用户信息
     * @param userDTO
     * @return
     */
    @Override
    public boolean insertOne(UserDTO userDTO) {
        return userService.insertOne(userDTO);
    }


    /***
     * 批量查询用户信息
     * @param userIdList
     * @return
     */
    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        return userService.batchQueryUserInfo(userIdList);
    }
}
