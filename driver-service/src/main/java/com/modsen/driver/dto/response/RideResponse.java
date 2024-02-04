package com.modsen.driver.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RideResponse(
        Long id,
        Long passengerId,
        String pickUpAddress,
        String destinationAddress,
        BigDecimal cost,
        boolean isDriverAvailable,
        Long driverId
) {
}
