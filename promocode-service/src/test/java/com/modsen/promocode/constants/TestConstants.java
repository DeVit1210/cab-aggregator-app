package com.modsen.promocode.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {
    public static final String PROMOCODE_NAME = "PROMO10";
    public static final int PROMOCODE_DISCOUNT_PERCENT = 10;
    public static final Long PROMOCODE_ID = 1L;
    public static final int UPDATED_DISCOUNT_PERCENT = 20;
    public static final Long PASSENGER_ID = 1L;
    public static final String INVALID_PROMOCODE_NAME = "NO_PROMO";

    public static class FieldNames {
        public static final String PROMOCODES_FIELD = "promocodes";
        public static final String QUANTITY_FIELD = "quantity";
        public static final String ID_FIELD = "id";
        public static final String PASSENGER_ID_FIELD = "passengerId";
        public static final String PROMOCODE_ID_FIELD = "promocodeId";
        public static final String PROMOCODE_NAME_FIELD = "name";
        public static final String DISCOUNT_PERCENT_FIELD = "discountPercent";
    }
}
