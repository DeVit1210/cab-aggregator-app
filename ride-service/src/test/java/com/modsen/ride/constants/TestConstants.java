package com.modsen.ride.constants;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class TestConstants {
    public static final Long RIDE_ID = 1L;
    public static final BigDecimal RIDE_COST = BigDecimal.TEN;
    public static final double RIDE_DISTANCE = 10.00;
    public static final String RIDE_PICKUP_ADDRESS = "ул. Гикало, 1";
    public static final String RIDE_DESTINATION_ADDRESS = "ул. Я.Коласа, 28";

    public static final Long PROMOCODE_ID = 1L;
    public static final int PROMOCODE_DISCOUNT_PERCENT = 20;
    public static final double PROMOCODE_DISCOUNT_PERCENT_DOUBLE = (double) PROMOCODE_DISCOUNT_PERCENT / 100;

    public static final Long PASSENGER_ID = 1L;
    public static final Long DRIVER_ID = 1L;

    public static class Url {
        public static final String AVAILABLE_DRIVER_RIDE = "/available/driver/";
        public static final String CONFIRMED_PASSENGER_RIDE = "/confirmed/passenger/";
        public static final String ACCEPT_RIDE = "/accept";
        public static final String DISMISS_RIDE = "/dismiss";
        public static final String NOTIFY_PASSENGER = "/notify-waiting";
        public static final String START_RIDE = "/start";
        public static final String CANCEL_RIDE = "/cancel";
        public static final String FINISH_RIDE = "/finish";
    }

    public static class FieldNames {
        public static final String ROLE_FIELD = "role";
        public static final String RIDE_STATUS_FIELD = "rideStatus";
        public static final String RIDES_FIELD = "rides";
        public static final String QUANTITY_FIELD = "quantity";
        public static final String ID_FIELD = "id";
        public static final String DRIVER_ID_FIELD = "driverId";
        public static final String PASSENGER_ID_FIELD = "passengerId";
        public static final String PERSON_ID_FIELD = "personId";
    }

    public static class KafkaConstants {
        public static final String AUTO_COMMIT_RESET = "latest";
        public static final String TRUSTED_PACKAGES_KEY = "spring.json.trusted.packages";
        public static final String TRUSTED_PACKAGES_VALUE = "*";
    }
}
