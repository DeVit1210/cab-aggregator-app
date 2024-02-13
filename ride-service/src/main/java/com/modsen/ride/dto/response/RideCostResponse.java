package com.modsen.ride.dto.response;

import com.modsen.ride.enums.RideDemand;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RideCostResponse(
        String pickUpAddress,
        String destinationAddress,
        double distanceInKm,
        RideDemand rideDemand,
        BigDecimal totalCost
) {
}
