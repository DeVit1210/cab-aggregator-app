package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.enums.DriverStatus;

public class DriverStatusChangeNotAllowedException extends RuntimeException {
    public DriverStatusChangeNotAllowedException(DriverStatus driverStatus) {
        super(String.format(MessageTemplates.STATUS_CHANGE_NOT_ALLOWED.getValue(), driverStatus.name()));
    }
}
