package com.modsen.ride.dto.response;

import com.modsen.ride.enums.RideStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ConfirmedRideResponse(
        Long id,
        Long passengerId,
        Long driverId,
        String pickUpAddress,
        String destinationAddress,
        BigDecimal rideCost,
        RideStatus rideStatus
) {
}