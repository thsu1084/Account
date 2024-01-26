package com.example.Account.config;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
@Slf4j
public class LocalRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis(){
        redisServer = RedisServer.builder()
            .port(redisPort)
            .setting("maxmemory 128M")
            .build();
        try {
            redisServer.start();
        }catch (Exception e){
            log.error("",e);
        }
    }

    @PreDestroy
    public void stopRedis(){
        if (redisServer != null){
            redisServer.stop();
        }
    }

}
