package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class DriverAccountAlreadyExistsException extends AlreadyExistsException {
    public DriverAccountAlreadyExistsException(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_ACCOUNT_ALREADY_EXISTS.getValue(), driverId));
    }
}
