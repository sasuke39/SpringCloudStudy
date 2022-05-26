package com.geekbang.coupon.calculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.geekbang"})
@EnableDiscoveryClient
public class CouponCalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponCalApplication.class, args);
    }
}
