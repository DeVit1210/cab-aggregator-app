package com.modsen.driver.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceMappings {
    public static final String DRIVER_CONTROLLER = "/drivers";
    public static final String RATING_SERVICE = "rating-service";
    public static final String RATING_BASE_URL = "/api/v1/ratings";
    public static final String ALL_AVERAGE_RATINGS_URL = "/average";
    public static final String AVERAGE_RATING_BY_ID_URL = "/average/{ratedPersonId}";
    public static final String PAYMENT_SERVICE = "payment-service";
    public static final String PAYMENT_BASE_URL = "/api/v1/payments";
    public static final String DRIVER_ACCOUNT_BY_ID_URL = "/driver/accounts/{accountId}";
}
