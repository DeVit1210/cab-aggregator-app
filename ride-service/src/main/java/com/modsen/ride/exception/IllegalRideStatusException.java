package com.modsen.ride.exception;

import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.exception.base.BadRequestException;

public class IllegalRideStatusException extends BadRequestException {
    public IllegalRideStatusException(RideStatus rideStatus) {
        super(rideStatus.getExceptionMessage());
    }
}
