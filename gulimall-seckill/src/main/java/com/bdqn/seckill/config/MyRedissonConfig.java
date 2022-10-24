package com.bdqn.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {

    // 配置 redsson
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        // java.lang.IllegalArgumentException: Redis url should start with redis:// or rediss:// (for SSL connection)
        config.useSingleServer().setAddress("redis://192.168.50.124:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

}
