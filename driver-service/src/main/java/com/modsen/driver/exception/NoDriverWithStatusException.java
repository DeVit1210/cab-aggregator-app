package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.exception.base.BadRequestException;

public class NoDriverWithStatusException extends BadRequestException {
    public NoDriverWithStatusException(DriverStatus status) {
        super(String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_STATUS.getValue(), status));
    }
}
