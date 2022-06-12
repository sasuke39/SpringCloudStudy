package com.geekbang.coupon.customer.mq;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xianhong
 * @date 2022/6/12
 */
@Getter
public class EventConstant {

    public static final String ADD_COUPON_EVENT = "addCoupon-out-0";

    public static final String ADD_COUPON_DELAY_EVENT = "addCouponDelay-out-0";

    public static final String DELETE_COUPON_EVENT = "deleteCoupon-out-0";
}
