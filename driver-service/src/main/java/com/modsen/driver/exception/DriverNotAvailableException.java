package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.exception.base.ConflictException;

public class DriverNotAvailableException extends ConflictException {
    public DriverNotAvailableException(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_NOT_AVAILABLE.getValue(), driverId));
    }
}
