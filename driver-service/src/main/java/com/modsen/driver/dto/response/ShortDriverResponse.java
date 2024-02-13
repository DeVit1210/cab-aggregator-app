package com.modsen.driver.dto.response;

import com.modsen.driver.enums.DriverStatus;
import lombok.Builder;

@Builder
public record ShortDriverResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        DriverStatus driverStatus
) {
}
