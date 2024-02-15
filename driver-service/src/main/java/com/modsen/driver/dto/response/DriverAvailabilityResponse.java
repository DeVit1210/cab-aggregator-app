package com.modsen.driver.dto.response;

public record DriverAvailabilityResponse(
        long totalDriverCount,
        long availableDriverCount
) {
}
