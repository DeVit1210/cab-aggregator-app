package com.modsen.rating.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceMappings {
    public static final String RATING_CONTROLLER = "/ratings";
    public static final String DRIVER_SERVICE = "driver-service";
    public static final String DRIVER_BASE_URL = "/api/v1/drivers";
    public static final String PASSENGER_SERVICE = "passenger-service";
    public static final String PASSENGER_BASE_URL = "/api/v1/passengers";
    public static final String RIDE_SERVICE = "ride-service";
    public static final String RIDE_BASE_URL = "/api/v1/rides";
    public static final String PASSENGER_BY_ID_URL = "/{passengerId}";
    public static final String DRIVER_BY_ID_URL = "/{driverId}";
    public static final String RIDE_BY_ID_URL = "/{rideId}";
}
