package com.geekbang.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * @author xianhong
 * @date 2022/6/11
 */

@Configuration
public class RedisLimitationConfig {

    // 限流的维度
    @Bean
    @Primary
    public KeyResolver remoteHostLimitationKey() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }

    //template服务限流规则
    @Bean("tempalteRateLimiter")
    public RedisRateLimiter templateRateLimiter() {
        return new RedisRateLimiter(10, 20);
    }

    // customer服务限流规则
    @Bean("customerRateLimiter")
    public RedisRateLimiter customerRateLimiter() {
        return new RedisRateLimiter(20, 40);
    }

    @Bean("defaultRateLimiter")
    @Primary
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(50, 100);
    }
}
