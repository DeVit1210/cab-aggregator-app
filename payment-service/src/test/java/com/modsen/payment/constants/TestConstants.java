package com.modsen.payment.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {
    public static final String BASE_URL = "/payments";
    public static final Long PASSENGER_ID = 1L;
    public static final Long DRIVER_ID = 1L;
    public static final Long CREDIT_CARD_ID = 1L;
    public static final Long CARD_HOLDER_ID = 1L;
    public static final Long RIDE_ID = 1L;

    public static class Stripe {
        public static final String CUSTOMER_ID = "cus_1234567890";
        public static final String CREDIT_CARD_ID = "pm_1234567890";
    }

    public static class CreditCard {
        public static final String NUMBER = "4242424242424242";
        public static final int MONTH_EXP = 1;
        public static final int YEAR_EXP = 30;
        public static final String CVC = "123";
    }

    public static class FieldNames {
        public static final String ROLE_FIELD = "role";
        public static final String CARD_HOLDER_ID_FIELD = "cardHolderId";
        public static final String QUANTITY_FIELD = "quantity";
        public static final String ID_FIELD = "id";
        public static final String RIDE_ID_FIELD = "rideId";
        public static final String PASSENGER_ID_FIELD = "passengerId";
        public static final String AMOUNT_FIELD = "amount";
        public static final String PAYOUT_LIST_FIELD = "payouts";
    }
}
