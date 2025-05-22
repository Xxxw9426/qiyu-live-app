package org.qiyu.live.user.interfaces;

import org.qiyu.live.user.dto.UserLoginDTO;
import org.qiyu.live.user.dto.UserPhoneDTO;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-13
 * @Description: 用户电话相关服务rpc接口
 * @Version: 1.0
 */

public interface IUserPhoneRPC {

    /***
     * 用户登录(未注册过则注册初始化)
     * 返回注册成功后的userId和token 或者登录失败的失败信息
     * @param phone
     * @return
     */
    UserLoginDTO login(String phone);


    /***
     * 根据用户手机号码查询用户电话关联用户信息(加入了缓存和缓存击穿的逻辑)
     * @param phone
     * @return
     */
    UserPhoneDTO queryByPhone(String phone);


    /***
     * 根据用户id批量查询用户的电话关联用户信息
     * @param userId
     * @return
     */
    List<UserPhoneDTO> queryByUserId(Long userId);
}
