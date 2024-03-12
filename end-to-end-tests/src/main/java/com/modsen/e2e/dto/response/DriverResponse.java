package com.modsen.e2e.dto.response;

import com.modsen.e2e.enums.DriverStatus;
import lombok.Builder;

@Builder
public record DriverResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        DriverStatus driverStatus,
        double averageRating,
        int ratesQuantity
) {
}
