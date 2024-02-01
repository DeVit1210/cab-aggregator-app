package com.modsen.payment.constants;

public interface ValidationConstants {
    String CARD_NUMBER_NOT_EMPTY = "card number cannot be empty!";
    String CARD_NUMBER_INVALID = "invalid card number format!";
    String EXPIRE_MONTH_INVALID = "invalid card expiration month format!";
    String EXPIRE_YEAR_INVALID = "invalid card expiration year format!";
    String CVC_INVALID = "invalid cvc format!";
    String AMOUNT_NOT_EMPTY = "amount cannot be empty!";
    String AMOUNT_INVALID = "amount has to be higher than zero!";
    String ID_NOT_EMPTY = "id cannot be empty!";
}
