package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;
import com.modsen.promocode.exception.base.BadRequestException;

public class PromocodeMissingForPassengerException extends BadRequestException {
    public PromocodeMissingForPassengerException(Long passengerId) {
        super(String.format(MessageTemplates.NO_PROMOCODE_FOR_PASSENGER.getValue(), passengerId));
    }
}
