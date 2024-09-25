package com.shywind.hqblog.Config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
    public class RedisConfig {
        // 设置Redis序列化方式，默认使用的JDKSerializer的序列化方式，效率低，这里我们使用 FastJsonRedisSerializer
        @Bean
        public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>(); // key序列化
            RedisSerializer stringSerializer = new StringRedisSerializer();
            redisTemplate.setKeySerializer(stringSerializer); // value序列化
            redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class)); // Hash key序列化
            redisTemplate.setHashKeySerializer(stringSerializer); // Hash value序列化
            redisTemplate.setHashValueSerializer(stringSerializer);
            redisTemplate.setConnectionFactory(redisConnectionFactory); return redisTemplate;
        }
    }