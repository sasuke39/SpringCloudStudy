package com.geekbang.coupon.customer.mq;

import com.geekbang.coupon.customer.api.beans.RequestCoupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author xianhong
 * @date 2022/6/12
 */

@Service
@Slf4j
public class CouponProducer {

    @Autowired
    private StreamBridge streamBridge;

    public void sendCoupon(RequestCoupon coupon) {
        log.info("sent: {}", coupon);
        streamBridge.send(EventConstant.ADD_COUPON_EVENT, coupon);
    }

    public void deleteCoupon(Long userId, Long couponId) {
        log.info("sent delete coupon event: userId={}, couponId={}", userId, couponId);
        streamBridge.send(EventConstant.DELETE_COUPON_EVENT, userId + "," + couponId);
    }


    // 使用延迟消息发送
    public void sendCouponInDelay(RequestCoupon coupon) {
        log.info("sent: {}", coupon);
        streamBridge.send(EventConstant.ADD_COUPON_DELAY_EVENT,
                MessageBuilder.withPayload(coupon)
                        .setHeader("x-delay", 50 * 1000)
                        .build());
    }

}

