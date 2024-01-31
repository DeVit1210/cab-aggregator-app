package com.modsen.payment.constants;

public interface ValidationConstants {
    String CARD_NUMBER_NOT_EMPTY = "card number cannot be empty!";
    String CARD_NUMBER_INVALID = "invalid card number format!";
    String EXPIRE_MONTH_INVALID = "invalid card expiration month format!";
    String EXPIRE_YEAR_INVALID = "invalid card expiration year format!";
    String CVC_INVALID = "invalid cvc format!";
    String PASSENGER_ID_NOT_EMPTY = "passenger id cannot be empty!";
    String CARD_HOLDER_ID_NOT_EMPTY = "card holder id cannot be empty!";
    String AMOUNT_NOT_EMPTY = "amount cannot be empty!";
    String AMOUNT_INVALID = "amount has to be higher than zero!";
    String RIDE_ID_NOT_EMPTY = "ride id cannot be empty!";
}
