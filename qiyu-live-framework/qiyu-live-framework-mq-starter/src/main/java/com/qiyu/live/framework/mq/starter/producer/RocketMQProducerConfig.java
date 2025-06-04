package com.qiyu.live.framework.mq.starter.producer;

import com.qiyu.live.framework.mq.starter.properties.RocketMQProducerProperties;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-02
 * @Description: rocketMQ生产者的配置类，做rocketmq生产者的初始化操作等
 * @Version: 1.0
 */
@Configuration
public class RocketMQProducerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQProducerConfig.class);

    @Resource
    private RocketMQProducerProperties rocketMQProducerProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public MQProducer mqProducer() {
        ThreadPoolExecutor asyncThreadPool = new ThreadPoolExecutor(100, 150, 3,
                TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(applicationName+":rmq-producer"+ ThreadLocalRandom.current().nextInt(1000));
                return thread;
            }
        });
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
        try {
            defaultMQProducer.setProducerGroup(rocketMQProducerProperties.getGroupName());
            defaultMQProducer.setNamesrvAddr(rocketMQProducerProperties.getNameSrv());
            defaultMQProducer.setRetryTimesWhenSendFailed(rocketMQProducerProperties.getRetryTimes());
            defaultMQProducer.setRetryTimesWhenSendAsyncFailed(rocketMQProducerProperties.getRetryTimes());
            // 设置rocketMQ中异步发送的线程池
            defaultMQProducer.setAsyncSenderExecutor(asyncThreadPool);
            defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
            defaultMQProducer.start();
            LOGGER.info("mq生产者启动成功,namesrv is {}", rocketMQProducerProperties.getNameSrv());
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
        return defaultMQProducer;
    }
}
