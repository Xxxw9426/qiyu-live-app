package org.idea.qiyu.live.framework.redis.starter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-01
 * @Description:
 * @Version: 1.0
 */


// 表明这是一个 Spring 配置类，类似于传统的 XML 配置文件，用于定义 Spring 应用程序中的 Bean
@Configuration
// 这是 Spring Boot 提供的条件注解，当类路径中存在 RedisTemplate 类时，才会加载这个配置类
@ConditionalOnClass(RedisTemplate.class)
public class RedisConfig {


    /**
     * 定义一个名为 redisTemplate 的 Bean，用于与 Redis 数据库进行交互
     * @param redisConnectionFactory Redis 连接工厂，通过 Spring 的依赖注入机制自动注入
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 创建一个 RedisTemplate 实例，用于操作 Redis 数据库
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 将 RedisConnectionFactory 设置到 RedisTemplate 中，以便通过连接工厂创建与 Redis 数据库的连接
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 创建一个自定义的 JSON 序列化器，用于将对象序列化为 JSON 字符串，以便存储到 Redis 中
        IGenericJackson2JsonRedisSerializer valueSerializer = new IGenericJackson2JsonRedisSerializer();
        // 创建一个 Spring Data Redis 提供的字符串序列化器，用于将字符串类型的键和哈希键进行序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 设置 RedisTemplate 的键序列化器为字符串序列化器
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 设置 RedisTemplate 的值序列化器为自定义的 JSON 序列化器
        redisTemplate.setValueSerializer(valueSerializer);
        // 设置 RedisTemplate 的哈希键序列化器为字符串序列化器
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 设置 RedisTemplate 的哈希值序列化器为自定义的 JSON 序列化器
        redisTemplate.setHashValueSerializer(valueSerializer);

        // 调用 RedisTemplate 的 afterPropertiesSet 方法，确保所有属性都已正确设置
        redisTemplate.afterPropertiesSet();
        // 返回配置好的 RedisTemplate 实例
        return redisTemplate;
    }
}

