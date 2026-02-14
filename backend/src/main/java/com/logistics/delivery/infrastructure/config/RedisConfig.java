package com.logistics.delivery.infrastructure.config;

import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(2);
        return Redisson.create(config);
    }

    @Bean
    public org.springframework.cache.CacheManager redisCacheManager(RedissonClient redisson) {
        return new org.redisson.spring.cache.RedissonSpringCacheManager(redisson);
    }
}
