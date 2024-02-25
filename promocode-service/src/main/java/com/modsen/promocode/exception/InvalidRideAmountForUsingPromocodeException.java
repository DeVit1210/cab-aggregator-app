package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;
import com.modsen.promocode.exception.base.BadRequestException;

public class InvalidRideAmountForUsingPromocodeException extends BadRequestException {
    public InvalidRideAmountForUsingPromocodeException(String promocodeName, int minRidesAmount, int quantity) {
        super(String.format(
                MessageTemplates.INVALID_RIDE_AMOUNT_FOR_PROMOCODE.getValue(),
                promocodeName,
                minRidesAmount,
                quantity
        ));
    }
}
