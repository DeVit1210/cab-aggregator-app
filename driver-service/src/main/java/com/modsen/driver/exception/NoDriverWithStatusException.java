package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.enums.DriverStatus;

public class NoDriverWithStatusException extends RuntimeException {
    public NoDriverWithStatusException(DriverStatus status) {
        super(String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_STATUS.getValue(), status));
    }
}
