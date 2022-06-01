package com.geekbang.coupon.customer;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author xianhong
 * @date 2022/5/31
 */



@org.springframework.context.annotation.Configuration
public class OpenfeignSentinelInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header("SentinelSource", "coupon-customer-serv");
    }
}
