package com.modsen.e2e.dto.response;

import com.modsen.e2e.enums.RideStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record RideResponse(
        Long id,
        Long passengerId,
        Long driverId,
        String pickUpAddress,
        String destinationAddress,
        BigDecimal rideCost,
        RideStatus rideStatus,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}