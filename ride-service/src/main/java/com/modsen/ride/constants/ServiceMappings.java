package com.modsen.ride.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceMappings {
    public static final String RIDE_CONTROLLER = "/rides";
    public static final String RIDE_OPERATIONS_CONTROLLER = "/ride-operations";
    public static final String RIDE_COST_CONTROLLER = "/rides/cost";

    public static class Url {
        public static final String DRIVER_AVAILABILITY_URL = "/availability";
        public static final String PASSENGER_BY_ID_URL = "/{passengerId}";
        public static final String STRIPE_CUSTOMER_BY_ID_URL = "/customers/{customerId}";
        public static final String DEFAULT_CARD_FOR_PASSENGER_URL = "/credit-cards/{passengerId}";
        public static final String NOT_CONFIRMED_PROMOCODE_FOR_PASSENGER_URL = "/appliance/{passengerId}";
        public static final String CONFIRMED_PROMOCODE_APPLIANCE = "/appliance/{promocodeId}";
    }
}
