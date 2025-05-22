package org.idea.qiyu.live.framework.redis.starter.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-01
 * @Description: 条件注入类，实现了根据类加载的路径判断是否要加载当前类
 * @Version: 1.0
 */

public class RedisKeyLoadMatch implements Condition {
    private final static Logger LOGGER =
            LoggerFactory.getLogger(RedisKeyLoadMatch.class);
    private static final String PREFIX = "qiyulive";
    @Override
    public boolean matches(ConditionContext context,
                           AnnotatedTypeMetadata metadata) {
        String appName =
                context.getEnvironment().getProperty("spring.application.name");
        if (appName == null) {
            LOGGER.error("没有匹配到应用名称，所以无法加载任何 RedisKeyBuilder对象");
            return false;
        }
        try {
            Field classNameField =
                    metadata.getClass().getDeclaredField("className");
            classNameField.setAccessible(true);
            String keyBuilderName = (String)
                    classNameField.get(metadata);
            List<String> splitList =
                    Arrays.asList(keyBuilderName.split("\\."));
            //忽略大小写，统一用qiyu开头命名
            String classSimplyName = PREFIX +
                    splitList.get(splitList.size() - 1).toLowerCase();
            boolean matchStatus =
                    classSimplyName.contains(appName.replaceAll("-", ""));
            LOGGER.info("keyBuilderClass is {},matchStatus is {}",
                    keyBuilderName, matchStatus);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
