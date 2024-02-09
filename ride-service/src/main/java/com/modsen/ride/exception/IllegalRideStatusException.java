package com.modsen.ride.exception;

import com.modsen.ride.enums.RideStatus;

public class IllegalRideStatusException extends RuntimeException {
    public IllegalRideStatusException(RideStatus rideStatus) {
        super(rideStatus.getExceptionMessage());
    }
}
