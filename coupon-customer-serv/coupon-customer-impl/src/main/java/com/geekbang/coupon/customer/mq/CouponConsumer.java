package com.geekbang.coupon.customer.mq;

import com.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.geekbang.coupon.customer.service.intf.CouponCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author xianhong
 * @date 2022/6/12
 */

@Slf4j
@Service
public class CouponConsumer {

    @Autowired
    private CouponCustomerService customerService;

    @Bean
    public Consumer<RequestCoupon> addCoupon() {
        return request -> {
            log.info("received: {}", request);
            customerService.requestCoupon(request);
        };
    }

    @Bean
    public Consumer<String> deleteCoupon() {
        return request -> {
            log.info("received: {}", request);
            List<Long> params = Arrays.stream(request.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            customerService.deleteCoupon(params.get(0), params.get(1));
        };
    }


    @Bean
    public Consumer<RequestCoupon> addCouponDelay() {
        return request -> {
            log.info("received: {}", request);
            customerService.requestCoupon(request);
        };
    }

}
