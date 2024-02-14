package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;

public class PromocodeMissingForPassengerException extends RuntimeException {
    public PromocodeMissingForPassengerException(Long passengerId) {
        super(String.format(MessageTemplates.NO_PROMOCODE_FOR_PASSENGER.getValue(), passengerId));
    }
}
