package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;

public class InvalidRideAmountForUsingPromocodeException extends RuntimeException {
    public InvalidRideAmountForUsingPromocodeException(String promocodeName, int minRidesAmount, int quantity) {
        super(String.format(
                MessageTemplates.INVALID_RIDE_AMOUNT_FOR_PROMOCODE.getValue(),
                minRidesAmount,
                promocodeName,
                quantity
        ));
    }
}
