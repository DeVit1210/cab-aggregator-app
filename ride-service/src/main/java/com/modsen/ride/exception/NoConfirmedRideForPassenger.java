package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;
import com.modsen.ride.exception.base.BadRequestException;

public class NoConfirmedRideForPassenger extends BadRequestException {
    public NoConfirmedRideForPassenger(Long passengerId) {
        super(String.format(MessageTemplates.NO_ACTIVE_RIDE_FOR_USER.getValue(), passengerId));
    }
}
