package com.modsen.e2e.constants;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class TestConstants {
    public static final String PICK_UP_ADDRESS = "ул. Есенина, 111";
    public static final String DESTINATION_ADDRESS = "ул. Гикало, 10";
    public static final BigDecimal RIDE_COST = BigDecimal.TEN;
    public static final Long PASSENGER_ID = 1L;
    public static final Long DRIVER_ID = 1L;
}
