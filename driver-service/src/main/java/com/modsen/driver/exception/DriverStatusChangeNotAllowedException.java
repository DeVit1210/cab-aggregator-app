package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.exception.base.BadRequestException;

public class DriverStatusChangeNotAllowedException extends BadRequestException {
    public DriverStatusChangeNotAllowedException(DriverStatus driverStatus) {
        super(String.format(MessageTemplates.STATUS_CHANGE_NOT_ALLOWED.getValue(), driverStatus.name()));
    }
}
