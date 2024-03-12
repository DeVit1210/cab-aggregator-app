package com.modsen.e2e.dto.response;

import com.modsen.e2e.enums.RideStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ShortRideResponse(
        Long id,
        Long passengerId,
        Long driverId,
        String pickUpAddress,
        String destinationAddress,
        BigDecimal rideCost,
        RideStatus rideStatus
) {
}