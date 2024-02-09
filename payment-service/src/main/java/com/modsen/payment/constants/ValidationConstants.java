package com.modsen.payment.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {
    public static final String CARD_NUMBER_NOT_EMPTY = "card number cannot be empty!";
    public static final String CARD_NUMBER_INVALID = "invalid card number format!";
    public static final String EXPIRE_MONTH_INVALID = "invalid card expiration month format!";
    public static final String EXPIRE_YEAR_INVALID = "invalid card expiration year format!";
    public static final String CVC_INVALID = "invalid cvc format!";
    public static final String AMOUNT_INVALID = "amount has to be higher than zero!";
    public static final String ID_NOT_NULL = "id cannot be empty!";
    public static final String ENUM_INVALID = "invalid enum value!";
    public static final String NAME_NOT_BLANK = "name cannot be blank!";
}

