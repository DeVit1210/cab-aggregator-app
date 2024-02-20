package com.modsen.passenger.exception;

import com.modsen.passenger.constants.MessageTemplates;

public class PassengerNotFoundException extends NotFoundException {
    public PassengerNotFoundException(String email) {
        super(String.format(MessageTemplates.PASSENGER_NOT_FOUND_BY_EMAIL.getValue(), email));
    }

    public PassengerNotFoundException(Long id) {
        super(String.format(MessageTemplates.PASSENGER_NOT_FOUND_BY_ID.getValue(), id));
    }
}
