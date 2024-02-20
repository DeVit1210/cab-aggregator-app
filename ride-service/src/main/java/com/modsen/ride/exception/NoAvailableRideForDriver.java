package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;
import com.modsen.ride.exception.base.BadRequestException;

public class NoAvailableRideForDriver extends BadRequestException {
    public NoAvailableRideForDriver(Long driverId) {
        super(String.format(MessageTemplates.NO_AVAILABLE_RIDE_FOR_DRIVER.getValue(), driverId));
    }
}
