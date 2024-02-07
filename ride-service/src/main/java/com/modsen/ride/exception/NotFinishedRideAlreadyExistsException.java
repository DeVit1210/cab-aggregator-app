package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;

public class NotFinishedRideAlreadyExistsException extends RuntimeException {
    public NotFinishedRideAlreadyExistsException(Long passengerId) {
        super(String.format(MessageTemplates.NOT_FINISHED_RIDE_EXISTS_FOR_PASSENGER.getValue(), passengerId));
    }
}
