package org.qiyu.live.user.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: rocketmq的producer端，用来写其producer的配置信息
 * @Version: 1.0
 */

// @ConfigurationProperties(prefix = "qiyu.rmq.producer") 这个注解的含义是指明我们这个类中的相关属性会通过application.yml文件配置
// 并且关于其的所有配置都会以我们指定的字符串为前缀
@ConfigurationProperties(prefix = "qiyu.rmq.producer")
@Configuration
public class RocketMQProducerProperties {

    // rocketMQ的nameServer的地址
    private String nameSrv;

    // 分组名称
    private String groupName;

    // 消息重发次数
    private int retryTimes;

    // 超时时间
    private int sendTimeOut;

    public String getNameSrv() {
        return nameSrv;
    }

    public void setNameSrv(String nameSrv) {
        this.nameSrv = nameSrv;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getSendTimeOut() {
        return sendTimeOut;
    }

    public void setSendTimeOut(int sendTimeOut) {
        this.sendTimeOut = sendTimeOut;
    }

    @Override
    public String toString() {
        return "RocketMQProducerProperties{" +
                "nameSrv='" + nameSrv + '\'' +
                ", groupName='" + groupName + '\'' +
                ", retryTimes=" + retryTimes +
                ", sendTimeOut=" + sendTimeOut +
                '}';
    }
}
