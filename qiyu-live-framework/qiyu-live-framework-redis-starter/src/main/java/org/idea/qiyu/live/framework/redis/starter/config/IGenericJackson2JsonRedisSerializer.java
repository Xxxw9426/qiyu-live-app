package org.idea.qiyu.live.framework.redis.starter.config;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/***
 * @Author: 萱子王
 * @CreateTime: 2025-05-01
 * @Description:  这个类主要用于在redis操作环境下对于Redis中的数据进行序列化和反序列化的操作
 *                 并且主要依赖于MapperFactory类中的ObjectMapper实例来完成大部分的序列化和反序列化工作，
 *                 并且在MapperFactory类中的ObjectMapper实例基础上针对Redis场景做了进一步的优化和定制，以满足 Redis 数据存储和读取的特殊需求。
 * @Version: 1.0
 */


// 定义一个名为 IGenericJackson2JsonRedisSerializer 的公共类，继承自 GenericJackson2JsonRedisSerializer
// 该类用于自定义对象的序列化过程，在使用 Spring Data Redis 与 Redis 交互时发挥作用
public class IGenericJackson2JsonRedisSerializer extends GenericJackson2JsonRedisSerializer {

    /**
     * 无参构造方法，用于初始化 IGenericJackson2JsonRedisSerializer 实例
     * 调用父类的构造方法，并传入通过 MapperFactory 的 newInstance 方法创建的 ObjectMapper 实例
     * 这样可以自定义 ObjectMapper 的配置，满足特定的序列化和反序列化需求
     *//*
    public IGenericJackson2JsonRedisSerializer() {
        // 调用父类 GenericJackson2JsonRedisSerializer 的构造函数，传入自定义的 ObjectMapper 实例
        super(MapperFactory.newInstance());
    }

    /**
     * 重写父类的 serialize 方法，实现自定义的序列化逻辑
     * 对于 String 和 Character 类型的对象，采用简单的字符串转字节数组的方式进行序列化
     * 对于其他类型的对象，使用父类的 JSON 序列化方式进行处理
     *
     * @param source 要进行序列化的对象
     * @return 序列化后的字节数组
     * @throws SerializationException 序列化过程中可能出现的异常
     */
    @Override
    public byte[] serialize(Object source) throws SerializationException {
        // 检查要序列化的对象是否不为 null，并且对象类型为 String 或者 Character
        if (source != null && ((source instanceof String) || (source instanceof Character))) {
            // 如果是 String 或 Character 类型，直接将对象转换为字符串，再将字符串转换为字节数组
            // 这样做避免了对简单类型进行 JSON 序列化，减少了序列化开销，提高了性能
            return source.toString().getBytes();
        }
        // 如果对象不是 String 或 Character 类型，调用父类的 serialize 方法进行默认的 JSON 序列化
        return super.serialize(source);
    }
}

