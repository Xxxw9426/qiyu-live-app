package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.HomePageVO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-01
 * @Description: 前端home页面相关的操作的service接口
 * @Version: 1.0
 */

public interface IHomePageService {


    /***
     * 更新前端home页面，返回home页面所需数据
     * @param userId
     * @return
     */
    HomePageVO initPage(Long userId);
}
