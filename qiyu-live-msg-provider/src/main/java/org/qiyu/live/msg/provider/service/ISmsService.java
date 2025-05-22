package org.qiyu.live.msg.provider.service;

import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-12
 * @Description: 短信业务service接口
 * @Version: 1.0
 */

public interface ISmsService {


    /***
     * 发送短信登录验证码接口
     * @param phone
     * @return
     */
    MsgSendResultEnum sendLoginCode(String phone);


    /***
     *  校验登录验证码
     * @param phone
     * @param code
     * @return
     */
    MsgCheckDTO checkLoginCode(String phone, Integer code);


    /***
     * 插入一条短信验证码记录
     * @param phone
     * @param code
     */
    void insertOne(String phone,Integer code);
}
