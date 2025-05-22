package org.qiyu.live.user.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.user.dto.UserLoginDTO;
import org.qiyu.live.user.dto.UserPhoneDTO;
import org.qiyu.live.user.interfaces.IUserPhoneRPC;
import org.qiyu.live.user.provider.service.IUserPhoneService;

import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-13
 * @Description: 用户电话相关服务rpc接口实现类
 * @Version: 1.0
 */

@DubboService
public class UserPhoneRPCImpl implements IUserPhoneRPC {


    @Resource
    private IUserPhoneService userPhoneService;


    /***
     * 用户登录(未注册过则注册初始化)
     * 返回注册成功后的userId和token 或者登录失败的失败信息
     * @param phone
     * @return
     */
    @Override
    public UserLoginDTO login(String phone) {
        return userPhoneService.login(phone);
    }


    /***
     * 根据用户手机号码查询用户电话关联用户信息(加入了缓存和缓存击穿的逻辑)
     * @param phone
     * @return
     */
    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        return userPhoneService.queryByPhone(phone);
    }


    /***
     * 根据用户id批量查询用户的电话关联用户信息
     * @param userId
     * @return
     */
    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        return userPhoneService.queryByUserId(userId);
    }
}
