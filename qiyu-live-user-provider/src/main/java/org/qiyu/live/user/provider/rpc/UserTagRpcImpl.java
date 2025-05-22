package org.qiyu.live.user.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.user.constants.UserTagsEnum;
import org.qiyu.live.user.interfaces.IUserTagRpc;
import org.qiyu.live.user.provider.service.IUserTagService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-06
 * @Description: 用户标签rpc接口实现类
 * @Version: 1.0
 */

@DubboService
public class UserTagRpcImpl implements IUserTagRpc {


    @Resource
    private IUserTagService userTagService;


    /**
     * 设置标签
     * @param userId
     * @param userTagsEnum
     * @return
     */
    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagService.setTag(userId,userTagsEnum);
    }


    /**
     * 取消标签
     * @param userId
     * @param userTagsEnum
     * @return
     */
    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagService.cancelTag(userId,userTagsEnum);
    }


    /**
     * 是否包含某个标签
     * @param userId
     * @param userTagsEnum
     * @return
     */
    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagService.containTag(userId,userTagsEnum);
    }
}
