package com.modsen.driver.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {
    public static final Long DRIVER_ID = 1L;
    public static final String DRIVER_EMAIL = "driver@gmail.com";
    public static final String DRIVER_PHONE_NUMBER = "+375 (25) 100-00-00";
    public static final String DRIVER_FIRST_NAME = "firstName";
    public static final String DRIVER_SECOND_NAME = "secondName";
    public static final String DRIVER_LICENCE_NUMBER = "1234567890";
    public static final String DRIVER_UPDATED_EMAIL = "updated-driver@gmail.com";
    public static final long AVAILABLE_DRIVERS_COUNT = 1L;
    public static final long TOTAL_DRIVERS_COUNT = 2L;
    public static final Long RIDE_ID = 1L;

    public static class FieldNames {
        public static final String DRIVERS_FIELD = "drivers";
        public static final String QUANTITY_FIELD = "quantity";
        public static final String ID_FIELD = "id";
        public static final String EMAIL_FIELD = "email";
        public static final String DRIVER_STATUS_FIELD = "driverStatus";
    }
}
