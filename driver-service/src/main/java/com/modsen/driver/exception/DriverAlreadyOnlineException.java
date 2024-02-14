package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.exception.base.ConflictException;

public class DriverAlreadyOnlineException extends ConflictException {
    public DriverAlreadyOnlineException(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_ALREADY_ONLINE.getValue(), driverId));
    }
}
