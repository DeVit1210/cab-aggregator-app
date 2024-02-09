package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;

public class PromocodeAlreadyAppliedException extends RuntimeException {
    public PromocodeAlreadyAppliedException(String promocodeName, Long passengerId) {
        super(String.format(
                MessageTemplates.PROMOCODE_ALREADY_APPLIED.getValue(),
                promocodeName,
                passengerId
        ));
    }
}
