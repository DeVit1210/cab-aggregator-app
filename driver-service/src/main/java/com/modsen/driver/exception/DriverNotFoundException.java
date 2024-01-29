package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_ID, driverId));
    }
}
