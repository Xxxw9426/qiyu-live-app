package org.qiyu.live.user.provider.service;

import org.qiyu.live.user.constants.UserTagsEnum;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-06
 * @Description: 用户标签service接口
 * @Version: 1.0
 */

public interface IUserTagService {

    /**
     * 设置标签
     * @param userId
     * @param userTagsEnum
     * @return
     */
    boolean setTag(Long userId, UserTagsEnum userTagsEnum);


    /**
     * 取消标签
     * @param userId
     * @param userTagsEnum
     * @return
     */
    boolean cancelTag(Long userId,UserTagsEnum userTagsEnum);


    /**
     * 是否包含某个标签
     * @param userId
     * @param userTagsEnum
     * @return
     */
    boolean containTag(Long userId,UserTagsEnum userTagsEnum);

}
