package org.qiyu.live.msg.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;
import org.qiyu.live.msg.interfaces.ISmsRpc;
import org.qiyu.live.msg.provider.service.ISmsService;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-12
 * @Description: 短信业务rpc接口实现类
 * @Version: 1.0
 */

@DubboService
public class SmsRpcImpl implements ISmsRpc {


    @Resource
    private ISmsService smsService;


    /***
     * 发送短信登录验证码接口
     * @param phone
     * @return
     */
    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        return smsService.sendLoginCode(phone);
    }


    /***
     *  校验登录验证码
     * @param phone
     * @param code
     * @return
     */
    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        return smsService.checkLoginCode(phone,code);
    }
}
