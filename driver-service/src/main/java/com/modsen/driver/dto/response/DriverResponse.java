package com.modsen.driver.dto.response;

import com.modsen.driver.enums.DriverStatus;
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
