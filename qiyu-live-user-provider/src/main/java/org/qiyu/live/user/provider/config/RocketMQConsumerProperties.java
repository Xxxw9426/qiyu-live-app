package org.qiyu.live.user.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: rocketMQ的消费方,用来写其consumer的配置信息
 * @Version: 1.0
 */

@ConfigurationProperties(prefix = "qiyu.rmq.consumer")
@Configuration
public class RocketMQConsumerProperties {

    // rocketMQ的nameServer的地址
    private String nameSrv;

    // 分组名称
    private String groupName;

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

    @Override
    public String toString() {
        return "RocketMQConsumerProperties{" +
                "nameSrv='" + nameSrv + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
