package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;
import com.modsen.promocode.exception.base.ConflictException;

public class PromocodeAlreadyAppliedException extends ConflictException {
    public PromocodeAlreadyAppliedException(String promocodeName, Long passengerId) {
        super(String.format(
                MessageTemplates.PROMOCODE_ALREADY_APPLIED.getValue(),
                promocodeName,
                passengerId
        ));
    }
}
