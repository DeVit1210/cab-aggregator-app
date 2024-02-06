package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;

public class NoConfirmedRideForPassenger extends RuntimeException {
    public NoConfirmedRideForPassenger(Long passengerId) {
        super(String.format(MessageTemplates.NO_ACTIVE_RIDE_FOR_USER.getValue(), passengerId));
    }
}
