package com.fintech.banktransaction.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * Redis 缓存全局配置类
 */
@Configuration
@EnableCaching // 把之前写在启动类上的这个注解挪到这里，统一管理
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // 自定义缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) //核心：所有缓存默认 10 分钟后自动销毁！
                .disableCachingNullValues();      //进阶防御：不缓存空值，防止黑客恶意查询不存在的账户（缓存穿透）

        // 使用自定义配置构建缓存管理器
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }
}