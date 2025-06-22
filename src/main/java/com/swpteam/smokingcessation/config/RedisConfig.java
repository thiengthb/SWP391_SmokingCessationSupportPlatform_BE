package com.swpteam.smokingcessation.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RedisConfig {

    private static final List<Map.Entry<String, Duration>> CACHE_LIST = List.of(
            Map.entry("BLOG_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("CATEGORY_LIST_CACHE", Duration.ofHours(1)),
            Map.entry("CATEGORY_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("COMMENT_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("BOOKING_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("TIMETABLE_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("MEMBERSHIP_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("MEMBERSHIP_LIST_CACHE", Duration.ofHours(1)),
            Map.entry("NOTIFICATION_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("GOAL_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("REVIEW_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("PLAN_PAGE_CACHE", Duration.ofHours(1)),
            Map.entry("FEEDBACK_PAGE_CACHE", Duration.ofHours(1)),

            Map.entry("BLOG_CACHE", Duration.ofMinutes(30)),
            Map.entry("CATEGORY_CACHE", Duration.ofHours(1)),
            Map.entry("PLAN_CACHE", Duration.ofMinutes(30)),
            Map.entry("PHASE_CACHE", Duration.ofMinutes(30)),
            Map.entry("MEMBERSHIP_CACHE", Duration.ofHours(1)),
            Map.entry("GOAL_CACHE", Duration.ofMinutes(15)),
            Map.entry("FEEDBACK_CACHE", Duration.ofMinutes(15)),
            Map.entry("SETTING_CACHE", Duration.ofMinutes(5)),
            Map.entry("NOTIFICATION_CACHE", Duration.ofMinutes(3)),
            Map.entry("COMMENT_CACHE", Duration.ofMinutes(3)),
            Map.entry("BOOKING_CACHE", Duration.ofMinutes(5)),
            Map.entry("REVIEW_CACHE", Duration.ofMinutes(5)),
            Map.entry("TIME_TABLE_CACHE", Duration.ofMinutes(5))
    );

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        for (Map.Entry<String, Duration> entry : CACHE_LIST) {
            cacheConfigurations.put(entry.getKey(),
                    defaultCacheConfig.entryTtl(entry.getValue()));
        }

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
