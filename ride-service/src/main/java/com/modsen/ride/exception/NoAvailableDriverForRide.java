package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;

public class NoAvailableDriverForRide extends RuntimeException {
    public NoAvailableDriverForRide(Long rideId) {
        super(String.format(MessageTemplates.NO_AVAILABLE_DRIVER_FOR_RIDE.getValue(), rideId));
    }
}
