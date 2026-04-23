package com.lrs.core.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 不配置Redisson自动注入也可以，Redisson自动注入，
 * @see org.redisson.spring.starter.RedissonAutoConfigurationV2
 * 没有使用 spring.redis.redisson前缀配置的，正常使用spring.redis即可
 * 优先使用 redisson前缀配置的，然后再到spring.redis
 */
@Slf4j
@Configuration
public class RedisConfig {

    /**
     * 通用对象RedisTemplate配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = createRedisTemplate(redisConnectionFactory,
                RedisSerializer.string(),
                createRedisJacksonSerializer());
        log.info("Generic RedisTemplate 初始化成功");
        return template;
    }

    private RedisSerializer<Object> createRedisJacksonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 遇到JSON里有、但 Java 对象里没有的字段时，不要报错，直接忽略！
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 带类名的序列化
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY
        );
//        return new GenericJackson2JsonRedisSerializer(objectMapper);
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }

    /**
     * 创建并配置RedisTemplate的通用方法
     */
    private <K, V> RedisTemplate<K, V> createRedisTemplate(
            RedisConnectionFactory connectionFactory,
            RedisSerializer<K> keySerializer,
            RedisSerializer<V> valueSerializer) {

        RedisTemplate<K, V> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
}
