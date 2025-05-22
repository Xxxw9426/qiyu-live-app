package org.qiyu.live.msg.provider.service.impl;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomUtils;
import org.idea.qiyu.live.framework.redis.starter.key.MsgProviderCacheKeyBuilder;
import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;
import org.qiyu.live.msg.provider.config.ApplicationProperties;
import org.qiyu.live.msg.provider.config.SmsTemplateIDEnum;
import org.qiyu.live.msg.provider.config.ThreadPoolManager;
import org.qiyu.live.msg.provider.dao.mapper.SmsMapper;
import org.qiyu.live.msg.provider.dao.po.SmsPO;
import org.qiyu.live.msg.provider.service.ISmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-12
 * @Description: 短信业务service接口实现类
 * @Version: 1.0
 */

@Service
public class SmsServiceImpl implements ISmsService {


    private static Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);


    @Resource
    private SmsMapper smsMapper;


    @Resource
    private RedisTemplate<String, Integer> redisTemplate;


    @Resource
    private MsgProviderCacheKeyBuilder msgProviderCacheKeyBuilder;


    @Resource
    private ApplicationProperties applicationProperties;


    /***
     * 发送短信登录验证码接口
     * @param phone
     * @return
     */
    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        // 首先判断phone是否为空，为空的话返回消息参数异常
        if (StringUtils.isEmpty(phone)) {
            return MsgSendResultEnum.MSG_PARAM_ERROR;
        }
        // TODO 发送短信验证码流程
        //  1. 生成验证码  规定验证码长度为6位，有效期60s，同一个手机号短时间内不能重复发送短信验证码
        //  2. 发送验证码
        //  3. 插入验证码发送记录
        // 首先判断缓存中是否有当前手机号的短信验证码，防止短时间内同一个手机号重复发送短信验证码
        String codeCacheKey=msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        if(redisTemplate.hasKey(codeCacheKey)) {
            logger.warn("该手机号短信验证码发送过于频繁，phone is {}",phone);
            return MsgSendResultEnum.SEND_FAIL;
        }
        // 生成验证码
        int code= RandomUtils.nextInt(1000,9999);
        // 将生成的验证码缓存在redis中
        redisTemplate.opsForValue().set(codeCacheKey,code,60, TimeUnit.SECONDS);
        // 发送验证码 由于通过第三方平台发送短信验证码是一个比较耗时的操作，因此我们通过线程池异步执行发送短信验证码
        ThreadPoolManager.commonAsyncPool.execute(()->{
            // 向线程池提交我们的模拟第三方平台发送短信验证码的方法
            boolean sendStatus = sendSmsToCCP(phone, code);
            // 如果第三方平台发送成功，在本地的数据库表中插入一条记录
            if(sendStatus) {
                insertOne(phone,code);
            }
        });
        return MsgSendResultEnum.SEND_SUCCESS;
    }


    /***
     *  校验登录验证码
     * @param phone
     * @param code
     * @return
     */
    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        // TODO 校验登录验证码的流程
        //  1. 校验参数
        //  2. 从Redis中获取当前手机号的登录验证码进行校验
        // 校验参数
        if (StringUtils.isEmpty(phone) || code == null || code < 1000) {
            return new MsgCheckDTO(false, "参数异常");
        }
        // 从Redis中获取当前手机号的登录验证码进行校验
        String key = msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        Integer cacheCode = redisTemplate.opsForValue().get(key);
        // 校验Redis中是否还有验证码或者验证码是否符合规范
        if (cacheCode == null || cacheCode < 1000) {
            return new MsgCheckDTO(false, "验证码已过期");
        }
        // 校验验证码与用户传入的验证码是否相同
        if (cacheCode.equals(code)) {
            redisTemplate.delete(key);
            return new MsgCheckDTO(true, "验证码校验成功");
        }
        return new MsgCheckDTO(false, "验证码校验失败");
    }


    /***
     * 插入一条短信验证码记录
     * @param phone
     * @param code
     */
    @Override
    public void insertOne(String phone, Integer code) {
        SmsPO smsPO = new SmsPO();
        smsPO.setPhone(phone);
        smsPO.setCode(code);
        smsMapper.insert(smsPO);
    }


    /***
     * 通过第三方平台发送短信验证码的方法
     * @param phone
     * @param code
     */
    private boolean sendSmsToCCP(String phone, Integer code) {
        try {
            //生产环境请求地址：app.cloopen.com
            String serverIp =applicationProperties.getSmsServerIp();
            //请求端口
            String serverPort = applicationProperties.getPort();
            //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
            String accountSId = applicationProperties.getAccountSId();
            String accountToken = applicationProperties.getAccountToken();
            //请使用管理控制台中已创建应用的APPID
            String appId = applicationProperties.getAppId();
            CCPRestSmsSDK sdk = new CCPRestSmsSDK();
            sdk.init(serverIp, serverPort);
            sdk.setAccount(accountSId, accountToken);
            sdk.setAppId(appId);
            sdk.setBodyType(BodyType.Type_JSON);
            String to = applicationProperties.getTestPhone();
            String templateId= SmsTemplateIDEnum.SMS_LOGIN_CODE_TEMPLATE.getTemplateId();
            String[] datas = {String.valueOf(code),"1"};
            String subAppend="1234";  //可选 扩展码，四位数字 0~9999
            String reqId= UUID.randomUUID().toString();  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
            //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
            HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas,subAppend,reqId);
            logger.info("phone is {},code is {}",phone,code);
            if("000000".equals(result.get("statusCode"))){
                //正常返回输出data包体信息（map）
                HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
                Set<String> keySet = data.keySet();
                for(String key:keySet){
                    Object object = data.get(key);
                    logger.info("key is {},object is {}",key,object);
                }
            }else{
                //异常返回输出错误码和错误信息
                logger.error("错误码={},错误信息={}",result.get("statusCode"),result.get("statusMsg"));
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("[sendSmsToCCP] error is",e);
            throw new RuntimeException(e);
        }
    }


    /**
     *  模拟发送短信过程，感兴趣的朋友可以尝试对接一些第三方的短信平台
     *  这里是通过短信sleep一秒来模拟通过第三方平台发送短信成功
     * @param phone
     * @param code
     */
    private boolean mockSendSms(String phone, Integer code) {
        try {
            logger.info(" ============= 创建短信发送通道中 ============= ,phone is {},code is {}", phone, code);
            Thread.sleep(1000);
            logger.info(" ============= 短信已经发送成功 ============= ");
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
