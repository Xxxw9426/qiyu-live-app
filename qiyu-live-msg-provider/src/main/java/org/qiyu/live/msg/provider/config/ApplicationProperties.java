package org.qiyu.live.msg.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-15
 * @Description: 读取Nacos中短信第三方平台的配置项的类
 * @Version: 1.0
 */
@ConfigurationProperties(prefix = "qiyu.sms.ccp")
@Configuration
public class ApplicationProperties {

    private String smsServerIp;

    private String port;

    private String accountSId;

    private String accountToken;

    private String appId;

    private String testPhone;

    public String getTestPhone() {
        return testPhone;
    }

    public void setTestPhone(String testPhone) {
        this.testPhone = testPhone;
    }

    public String getSmsServerIp() {
        return smsServerIp;
    }

    public void setSmsServerIp(String smsServerIp) {
        this.smsServerIp = smsServerIp;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAccountSId() {
        return accountSId;
    }

    public void setAccountSId(String accountSId) {
        this.accountSId = accountSId;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "smsServerIp='" + smsServerIp + '\'' +
                ", port=" + port +
                ", accountSId='" + accountSId + '\'' +
                ", accountToken='" + accountToken + '\'' +
                ", appId='" + appId + '\'' +
                ", testPhone='" + testPhone + '\'' +
                '}';
    }
}
