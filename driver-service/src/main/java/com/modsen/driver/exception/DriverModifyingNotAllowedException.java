package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.exception.base.BadRequestException;

public class DriverModifyingNotAllowedException extends BadRequestException {
    public DriverModifyingNotAllowedException(Long driverId) {
        super(String.format(MessageTemplates.DRIVER_MODIFYING_NOT_ALLOWED.getValue(), driverId));
    }
}
