package com.modsen.ride.constants;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class TestConstants {
    public static final Long RIDE_ID = 1L;
    public static final Long PASSENGER_ID = 1L;
    public static final Long DRIVER_ID = 1L;
    public static final Long PROMOCODE_ID = 1L;
    public static final BigDecimal RIDE_COST = BigDecimal.TEN;
    public static final double RIDE_DISTANCE = 10.0;
    public static final int PROMOCODE_DISCOUNT_PERCENT = 20;
    public static final double PROMOCODE_DISCOUNT_PERCENT_DOUBLE = (double) PROMOCODE_DISCOUNT_PERCENT / 100;
}
