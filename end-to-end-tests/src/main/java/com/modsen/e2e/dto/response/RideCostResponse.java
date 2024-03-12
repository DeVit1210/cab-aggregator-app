package com.modsen.e2e.dto.response;

import com.modsen.e2e.enums.RideDemand;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RideCostResponse(
        Long passengerId,
        String pickUpAddress,
        String destinationAddress,
        double distanceInKm,
        RideDemand rideDemand,
        BigDecimal rideCost,
        BigDecimal discountedCost
) {
}