package org.qiyu.live.msg.interfaces;

import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-12
 * @Description: 短信服务的rpc接口
 * @Version: 1.0
 */

public interface ISmsRpc {


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

}
