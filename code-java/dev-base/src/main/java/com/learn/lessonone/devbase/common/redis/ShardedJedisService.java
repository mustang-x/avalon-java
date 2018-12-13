package com.learn.lessonone.devbase.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.io.Serializable;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/13 22:39
 */
@Component
@Slf4j
public class ShardedJedisService {

    @Autowired
    private ShardedJedisPool shardedJedisPool;

    private RedisSerializer<String> keySerializer = new StringRedisSerializer();

    public <T extends Serializable> boolean setValue(String key, T value) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedJedisPool.getResource();
            shardedJedis.setnx(this.keySerializer.serialize(key), RedisSerializerFactory.getRedisSerializer("jdk").serialize(value));
        } catch (Exception e) {
            log.error("set error.key={}", key, e);
            shardedJedis.close();
        } finally {
            shardedJedis.close();
        }
        return false;
    }

}