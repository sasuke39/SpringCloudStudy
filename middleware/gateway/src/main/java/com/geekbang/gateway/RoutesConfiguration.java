package com.geekbang.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @author xianhong
 * @date 2022/6/11
 */

@Configuration
public class RoutesConfiguration {


    @Autowired
    private KeyResolver hostAddrKeyResolver;

    @Autowired
    @Qualifier("customerRateLimiter")
    private RateLimiter customerRateLimiter;

    @Autowired
    @Qualifier("tempalteRateLimiter")
    private RateLimiter templateRateLimiter;


    @Bean
    public RouteLocator declare(RouteLocatorBuilder builder) {
        return builder.routes(
                ).route(route -> route
                        .order(1)
                        .path("/gateway/template/**")
                        .filters(f -> f.stripPrefix(1)
                                // 修改Request参数
                                .removeRequestHeader("mylove")
                                .addRequestHeader("myLove", "u")
                                .removeRequestParameter("urLove")
                                .addRequestParameter("urLove", "me")
                                // response系列参数 不一一列举了
                                .removeResponseHeader("responseHeader")
                        )
                        .uri("lb://coupon-template-serv")
                ).route(route -> route
                        .path("/gateway/calculator/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(limiter -> {
                                    limiter.setRateLimiter(templateRateLimiter);
                                }))
                        .uri("lb://coupon-calculation-serv")
                ).route(route -> route.path("/gateway/coupon-customer/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(limiter -> {
                                            limiter.setKeyResolver(hostAddrKeyResolver);
                                            limiter.setRateLimiter(customerRateLimiter);
                                            // 限流失败后返回的HTTP status code
                                            limiter.setStatusCode(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                                        }
                                )
                        )
                        .uri("lb://coupon-customer-serv")).
                build();
    }
}
