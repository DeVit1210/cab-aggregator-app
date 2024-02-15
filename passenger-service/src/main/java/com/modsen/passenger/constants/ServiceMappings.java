package com.modsen.passenger.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceMappings {
    public static final String PASSENGER_CONTROLLER = "/passengers";
    public static final String RATING_SERVICE = "rating-service";
    public static final String RATING_BASE_URL = "/api/v1/ratings";
    public static final String ALL_AVERAGE_RATINGS_URL = "/average";
    public static final String AVERAGE_RATING_BY_ID_URL = "/average/{ratedPersonId}";
}
