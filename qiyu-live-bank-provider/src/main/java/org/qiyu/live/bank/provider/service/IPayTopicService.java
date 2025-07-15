package org.qiyu.live.bank.provider.service;

import org.qiyu.live.bank.provider.dao.po.PayTopicPO;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-06-08
 * @Description: 支付回调请求对应mq主题相关操作service接口
 * @Version: 1.0
 */

public interface IPayTopicService {


    /***
     * 根据业务码获取支付回调请求对应mq主题相关数据实体类
     * @param code
     * @return
     */
    PayTopicPO getByCode(Integer code);
}
