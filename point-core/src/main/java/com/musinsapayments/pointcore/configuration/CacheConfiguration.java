package com.musinsapayments.pointcore.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class CacheConfiguration {

    public static final String POINT_CACHE = "points";
    public static final String USER_CACHE = "users";

    @Getter
    @AllArgsConstructor
    public static class CacheProperty {
        private String cacheName;
        private Integer ttl;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {

        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator    // Object 타입을 사용할 경우 필요
                .builder()
                .allowIfSubType(Object.class)
                .build();

        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)    // Entity 에 없는 필드가 있어도 오류가 나지 않도록 설정
                .registerModule(new JavaTimeModule())   // Entity 에 LocalDateTime 이 있을 경우 필요
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)  // Object 타입을 사용할 경우 필요
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        List<CacheProperty> cacheProperties = List.of(
                new CacheProperty(POINT_CACHE, 300),    // 5분
                new CacheProperty(USER_CACHE, 3600)     // 1시간
        );

        return (builder ->
                cacheProperties.forEach(props ->
                        builder.withCacheConfiguration(props.getCacheName(), RedisCacheConfiguration
                                .defaultCacheConfig()
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)))
                                .entryTtl(Duration.ofSeconds(props.getTtl()))
                        )
                )
        );
    }
}
