package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class DriverAccountAlreadyExists extends RuntimeException {
    public DriverAccountAlreadyExists(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_ACCOUNT_ALREADY_EXISTS.getValue(), driverId));
    }
}
