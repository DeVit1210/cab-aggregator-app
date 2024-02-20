package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;
import com.modsen.ride.exception.base.BadRequestException;

public class NoAvailableDriverForRide extends BadRequestException {
    public NoAvailableDriverForRide(Long rideId) {
        super(String.format(MessageTemplates.NO_AVAILABLE_DRIVER_FOR_RIDE.getValue(), rideId));
    }
}
