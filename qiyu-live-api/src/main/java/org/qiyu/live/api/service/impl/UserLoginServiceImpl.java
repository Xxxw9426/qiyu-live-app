package org.qiyu.live.api.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.account.interfaces.IAccountTokenRpc;
import org.qiyu.live.api.error.QiyuApiError;
import org.qiyu.live.api.service.IUserLoginService;
import org.qiyu.live.api.vo.UserLoginVO;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;
import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;
import org.qiyu.live.msg.interfaces.ISmsRpc;
import org.qiyu.live.user.dto.UserLoginDTO;
import org.qiyu.live.user.interfaces.IUserPhoneRPC;
import org.qiyu.live.web.starter.error.ErrorAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-14
 * @Description: 用户登录API层service接口实现类
 * @Version: 1.0
 */
@Service
public class UserLoginServiceImpl implements IUserLoginService {


    // 校验电话号码的正则表达式
    private static String PHONE_REG = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";


    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginServiceImpl.class);


    // 发送短信验证码的rpc接口
    @DubboReference
    private ISmsRpc smsRpc;


    // 用户登录注册的rpc接口
    @DubboReference
    private IUserPhoneRPC userPhoneRPC;


    @DubboReference
    private IAccountTokenRpc accountTokenRPC;


    /***
     * 发送短信验证码
     * @param phone
     * @return
     */
    @Override
    public WebResponseVO sendLoginCode(String phone) {
        // 校验手机号码
        ErrorAssert.isNotBlank(phone, QiyuApiError.PHONE_NOT_BLANK);
        ErrorAssert.isTrue(Pattern.matches(PHONE_REG, phone),QiyuApiError.PHONE_IN_VALID);
        // 调用发送短信验证码的rpc接口中的方法
        MsgSendResultEnum msgSendResultEnum = smsRpc.sendLoginCode(phone);
        // 发送成功
        if (msgSendResultEnum == MsgSendResultEnum.SEND_SUCCESS) {
            return WebResponseVO.success();
        }
        // 发送失败
        return WebResponseVO.sysError("短信发送太频繁，请稍后再试");
    }


    /***
     * 登录请求
     * 该方法中首先要校验验证码是否合法，然后判断用户是否注册过-> 初始化注册/老用户登录
     * @param phone
     * @param code
     * @param response
     * @return
     */
    @Override
    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
        // 校验手机号码的格式
        ErrorAssert.isNotBlank(phone, QiyuApiError.PHONE_NOT_BLANK);
        ErrorAssert.isTrue(Pattern.matches(PHONE_REG, phone),QiyuApiError.PHONE_IN_VALID);
        // 校验验证码的格式
        ErrorAssert.isTrue(code != null && code >= 1000,QiyuApiError.LOGIN_CODE_IN_VALID);
        // 调用校验短信验证码的rpc接口中的方法
        MsgCheckDTO msgCheckDTO = smsRpc.checkLoginCode(phone, code);
        // 校验短信验证码失败
        if (!msgCheckDTO.isCheckStatus()) {
            return WebResponseVO.bizError(msgCheckDTO.getDesc());
        }
        // 验证码校验通过，直接调用用户登录注册rpc接口中的用户手机号登录(未注册过则注册)方法
        UserLoginDTO userLoginDTO = userPhoneRPC.login(phone);
        if(!userLoginDTO.isLoginSuccess()) {
            LOGGER.error("login has error,phone is {}",phone);
            // 极低概率发生，如果真有问题，提示系统异常
            return WebResponseVO.sysError();
        }
        // 用户登录成功，调用account服务给前端返回token
        String token=accountTokenRPC.createAndSaveLoginToken(userLoginDTO.getUserId());
        // 在这里，我们返回给前端的token会被前端写入cookie，但是cookie其实是有一些参数需要我们调控的
        // 如domain,path(路径),maxAge(生存有效时间)，为了维护的方便，我们将对这些参数的调控放在后端进行
        // 将我们生成的token写入cookie
        Cookie cookie = new Cookie("qytk",token);
        // http://app.qiyu.live.com/html/qiyu_live_list_room.html  前端被访问的域名和路径
        // http://api.qiyu.live.com/live/api/userLogin/sendLoginCode   前端访问后端的域名和路径
        // 设置了前端和后端访问域名中相同的一段，这样就可以保证请求携带的cookie在两个域名之间可以传递
        cookie.setDomain("127.0.0.1");
        // 设置path为"/"说明允许该域名下的所有路径都可以携带cookie
        cookie.setPath("/");
        // cookie有效期，一般他的默认单位是秒
        cookie.setMaxAge(30 * 24 * 3600);
        // 加上它，不然web浏览器不会将cookie自动记录下
        response.addCookie(cookie);
        return WebResponseVO.success(ConvertBeanUtils.convert(userLoginDTO, UserLoginVO.class));
    }
}
