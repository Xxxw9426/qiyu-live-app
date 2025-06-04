package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.resp.ImConfigVO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-03
 * @Description: 前端接入IM服务器相关请求的service接口
 * @Version: 1.0
 */

public interface ImService {


    /***
     * 返回前端接入IM服务器需要的用户认证token和IM服务器地址
     * @return
     */
    ImConfigVO getImConfig();
}
