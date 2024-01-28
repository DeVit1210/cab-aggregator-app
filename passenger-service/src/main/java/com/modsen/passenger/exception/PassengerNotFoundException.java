package com.modsen.passenger.exception;

public class PassengerNotFoundException extends RuntimeException {
    public PassengerNotFoundException(String message) {
        super(message);
    }

    public PassengerNotFoundException(Long id) {
        super(String.format("Passenger with id %s was not found!", id));
    }
}
