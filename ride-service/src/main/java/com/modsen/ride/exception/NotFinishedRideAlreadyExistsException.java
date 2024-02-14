package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;
import com.modsen.ride.exception.base.ConflictException;

public class NotFinishedRideAlreadyExistsException extends ConflictException {
    public NotFinishedRideAlreadyExistsException(Long passengerId) {
        super(String.format(MessageTemplates.NOT_FINISHED_RIDE_EXISTS_FOR_PASSENGER.getValue(), passengerId));
    }
}
