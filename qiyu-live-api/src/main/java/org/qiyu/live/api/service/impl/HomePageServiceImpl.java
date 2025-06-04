package org.qiyu.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.IHomePageService;
import org.qiyu.live.api.vo.HomePageVO;
import org.qiyu.live.user.constants.UserTagsEnum;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.interfaces.IUserRpc;
import org.qiyu.live.user.interfaces.IUserTagRpc;
import org.springframework.stereotype.Service;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-01
 * @Description: 前端home页面相关的操作的service接口实现类
 * @Version: 1.0
 */
@Service
public class HomePageServiceImpl implements IHomePageService {


    @DubboReference
    private IUserRpc userRpc;


    @DubboReference
    private IUserTagRpc userTagRpc;


    /***
     * 更新前端home页面，返回home页面所需数据
     * @param userId
     * @return
     */
    @Override
    public HomePageVO initPage(Long userId) {
        UserDTO userDTO = userRpc.getUserById(userId);
        System.out.println(userDTO);
        HomePageVO homePageVO = new HomePageVO();
        homePageVO.setLoginStatus(false);
        if (userId != null) {
            homePageVO.setAvatar(userDTO.getAvatar());
            homePageVO.setUserId(userDTO.getUserId());
            homePageVO.setNickName(userDTO.getNickName());
            //VIP用户才能开播
            homePageVO.setShowStartLivingBtn(userTagRpc.containTag(userId, UserTagsEnum.IS_VIP));
        }
        return homePageVO;
    }
}
