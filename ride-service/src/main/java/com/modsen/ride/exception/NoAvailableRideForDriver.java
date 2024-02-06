package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;

public class NoAvailableRideForDriver extends RuntimeException {
    public NoAvailableRideForDriver(Long driverId) {
        super(String.format(MessageTemplates.NO_AVAILABLE_RIDE_FOR_DRIVER.getValue(), driverId));
    }
}
