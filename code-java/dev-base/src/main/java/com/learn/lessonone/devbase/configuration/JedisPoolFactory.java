package com.learn.lessonone.devbase.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Arrays;
import java.util.List;

/**
 * Description:
 * <p>
 * Author:  xingye_tang@sui.com
 * Date:  2018/12/13 22:20
 */
@Configuration
public class JedisPoolFactory {

    //自动注入redis配置属性文件
    @Autowired
    private RedisProperties properties;

    @Bean
    public JedisPool jedisPool(){
        JedisPoolConfig config = new JedisPoolConfig(); //Jedis池配置
        config.setMaxIdle(properties.getPool().getMaxIdle());
        config.setMaxTotal(properties.getPool().getMaxActive());
        config.setMaxWaitMillis(properties.getPool().getMaxWait());
        JedisPool pool = new JedisPool(config,properties.getHost(),properties.getPort(),properties.getTimeout());
        return pool;
    }

    @Bean
    public ShardedJedisPool shardedJedisPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(properties.getPool().getMaxIdle());
        config.setMaxTotal(properties.getPool().getMaxActive());
        config.setMaxWaitMillis(properties.getPool().getMaxWait());
        JedisShardInfo shardInfo = new JedisShardInfo(properties.getHost(), properties.getPort(), properties.getTimeout());
        shardInfo.setPassword(properties.getPassword());
        List<JedisShardInfo> infoList = Arrays.asList(shardInfo);
        ShardedJedisPool pool = new ShardedJedisPool(config, infoList);
        return pool;
    }

}