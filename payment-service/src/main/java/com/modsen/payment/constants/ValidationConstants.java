package com.modsen.payment.constants;

public interface ValidationConstants {
    String CARD_NUMBER_NOT_EMPTY = "card number cannot be empty!";
    String CARD_NUMBER_INVALID = "invalid card number format!";
    String EXPIRE_MONTH_INVALID = "invalid card expiration month format!";
    String EXPIRE_YEAR_INVALID = "invalid card expiration year format!";
    String CVC_INVALID = "invalid cvc format!";
    String AMOUNT_INVALID = "amount has to be higher than zero!";
    String ID_NOT_NULL = "id cannot be empty!";
    String ENUM_INVALID = "invalid enum value!";
    String NAME_NOT_BLANK = "name cannot be blank!";
}
