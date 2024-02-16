package com.modsen.rating.dto.response;

import com.modsen.rating.enums.DriverStatus;
import lombok.Builder;

@Builder
public record DriverResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        DriverStatus driverStatus
) {
}