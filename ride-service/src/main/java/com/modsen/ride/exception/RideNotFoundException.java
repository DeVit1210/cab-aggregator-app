package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;

public class RideNotFoundException extends RuntimeException {
    public RideNotFoundException(Long rideId) {
        super(String.format(MessageTemplates.RIDE_NOT_FOUND_BY_ID.getValue(), rideId));
    }
}
