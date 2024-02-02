package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class AccountNotFoundForDriverIdException extends RuntimeException {
    public AccountNotFoundForDriverIdException(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_ACCOUNT_NOT_FOUND_FOR_DRIVER_ID.getValue(), driverId));
    }
}
