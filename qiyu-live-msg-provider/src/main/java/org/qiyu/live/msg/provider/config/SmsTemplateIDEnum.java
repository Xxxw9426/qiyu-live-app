package org.qiyu.live.msg.provider.config;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-15
 * @Description: 第三方短信发送模板id的枚举类
 * @Version: 1.0
 */

public enum SmsTemplateIDEnum {

    SMS_LOGIN_CODE_TEMPLATE("1","登录验证码短信模板");

    String templateId;

    String desc;

    SmsTemplateIDEnum(String templateId, String desc) {
        this.templateId = templateId;
        this.desc = desc;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getDesc() {
        return desc;
    }
}
