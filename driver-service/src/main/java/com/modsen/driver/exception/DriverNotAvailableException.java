package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;

public class DriverNotAvailableException extends RuntimeException {
    public DriverNotAvailableException(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_NOT_AVAILABLE.getValue(), driverId));
    }
}
