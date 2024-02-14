package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.exception.base.NotFoundException;

public class DriverNotFoundException extends NotFoundException {
    public DriverNotFoundException(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_ID.getValue(), driverId));
    }
}
