package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;

public class DriverAlreadyOnlineException extends RuntimeException {
    public DriverAlreadyOnlineException(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_ALREADY_ONLINE.getValue(), driverId));
    }
}
