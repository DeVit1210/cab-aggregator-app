package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;
import com.modsen.ride.exception.base.NotFoundException;

public class RideNotFoundException extends NotFoundException {
    public RideNotFoundException(Long rideId) {
        super(String.format(MessageTemplates.RIDE_NOT_FOUND_BY_ID.getValue(), rideId));
    }
}
