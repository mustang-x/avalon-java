package com.learn.lessonone.devbase.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/13 23:13
 */
@Slf4j
public class RedisSerializerFactory {

    private static final RedisSerializer<Object> JDK_SERIALIZATION = new JdkSerializationRedisSerializer();

    private static final RedisSerializer<Object> HESSIAN2_SERIALIZATION = new Hessian2SerializationRedisSerializer();

    public static RedisSerializer<Object> getRedisSerializer(String type) {

        if (type == null) {
            log.error("未找到redis序列化配置，请技术在配置中心appsetting配置redis_serializer_type，默认使用JDK序列化工具");
            return JDK_SERIALIZATION;
        }

        if ("hessian2".equals(type)) {
            return HESSIAN2_SERIALIZATION;
        } else if ("jdk".equals(type)) {
            return JDK_SERIALIZATION;
        } else {
            log.error("未找到redis序列化工具对应配置，type={}", type);
            return JDK_SERIALIZATION;
        }
    }
}