package org.idea.qiyu.live.framework.redis.starter.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.cache.support.NullValue;
import org.springframework.util.StringUtils;

import java.io.IOException;


/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-01
 * @Description: MapperFactory类的主要作用是创建和配置ObjectMapper实例，
 *               用于将Java对象转换为JSON格式（序列化）以及将JSON数据转换回Java对象（反序列化）。
 * @Version: 1.0
 */

// 定义一个名为 MapperFactory 的公共类，该类主要用于创建和配置 Jackson 的 ObjectMapper 实例
// ObjectMapper 是 Jackson 库中用于 Java 对象和 JSON 数据之间序列化和反序列化的核心类
public class MapperFactory {


    /**
     * 静态方法，用于创建并初始化一个 ObjectMapper 实例
     * 调用 initMapper 方法，传入新创建的 ObjectMapper 实例和 null 作为 classPropertyTypeName 参数
     * @return 初始化后的 ObjectMapper 实例
     */

    public static ObjectMapper newInstance() {
        // 创建一个新的 ObjectMapper 实例，并调用 initMapper 方法进行初始化
        return initMapper(new ObjectMapper(), (String) null);
    }


    /**
     * 私有静态方法，用于对传入的 ObjectMapper 实例进行配置
     * @param mapper 要配置的 ObjectMapper 实例
     * @param classPropertyTypeName 用于指定默认类型信息的属性名，可以为 null
     * @return 配置好的 ObjectMapper 实例
     */

    private static ObjectMapper initMapper(ObjectMapper mapper, String classPropertyTypeName) {
        // 注册一个新的 SimpleModule，并添加自定义的 MapperNullValueSerializer 序列化器
        // MapperNullValueSerializer 用于处理 NullValue 类型的序列化
        mapper.registerModule(new SimpleModule().addSerializer(new MapperNullValueSerializer(classPropertyTypeName)));

        // 检查 classPropertyTypeName 是否有值
        if (StringUtils.hasText(classPropertyTypeName)) {
            // 如果有值，启用默认类型信息，并指定类型信息的属性名
            mapper.enableDefaultTypingAsProperty(DefaultTyping.NON_FINAL, classPropertyTypeName);
        } else {
            // 如果没有值，启用默认类型信息，类型信息以属性的形式添加
            mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
        }

        // 禁用反序列化时遇到未知属性抛出异常的功能
        // 这样在反序列化时，如果遇到 JSON 数据中存在 Java 对象没有的属性，不会抛出异常
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 返回配置好的 ObjectMapper 实例
        return mapper;
    }


    /**
     * 私有静态内部类，继承自 StdSerializer<NullValue>
     * 用于处理 NullValue 类型的序列化，为其添加默认类型信息所需的类信息
     * 这样可以确保在反序列化时能够正确识别和重建 NullValue 对象
     */

    private static class MapperNullValueSerializer extends StdSerializer<NullValue> {
        // 序列化版本号，用于保证序列化和反序列化的兼容性
        private static final long serialVersionUID = 1999052150548658808L;
        // 用于指定类型信息的属性名
        private final String classIdentifier;


        /**
         * 构造方法，接受一个 classIdentifier 参数
         * 如果该参数不为空，则使用该参数作为类型信息的属性名
         * 否则，使用默认的 "@class" 作为类型信息的属性名
         * @param classIdentifier 类型信息的属性名，可以为 null
         */

        MapperNullValueSerializer(String classIdentifier) {
            // 调用父类的构造方法，指定处理的对象类型为 NullValue
            super(NullValue.class);
            // 如果 classIdentifier 有值，则使用该值
            // 否则，使用默认的 "@class" 作为类型信息的属性名
            this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
        }


        /**
         * 重写父类的 serialize 方法，用于将 NullValue 类型的对象序列化为 JSON 对象
         * 包含类型信息，以便在反序列化时能够正确识别和重建对象
         * @param value 要序列化的 NullValue 对象
         * @param jgen JSON 生成器，用于生成 JSON 数据
         * @param provider 序列化提供器，提供序列化所需的上下文信息
         * @throws IOException 序列化过程中可能抛出的 IO 异常
         */

       @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            // 开始写入 JSON 对象
            jgen.writeStartObject();
            // 写入类型信息的属性名和属性值（NullValue 类的全限定名）
            jgen.writeStringField(classIdentifier, NullValue.class.getName());
            // 结束写入 JSON 对象
            jgen.writeEndObject();
        }
    }
}

