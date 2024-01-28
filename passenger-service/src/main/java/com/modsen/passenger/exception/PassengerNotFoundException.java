package com.modsen.passenger.exception;

import com.modsen.passenger.constants.MessageTemplates;

public class PassengerNotFoundException extends RuntimeException {
    public PassengerNotFoundException(String message) {
        super(message);
    }

    public PassengerNotFoundException(Long id) {
        super(String.format(MessageTemplates.PASSENGER_NOT_FOUND_BY_ID, id));
    }
}
