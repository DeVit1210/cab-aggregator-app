package com.modsen.ride.dto.response;

public record DriverAvailabilityResponse(
        long totalDriverCount,
        long availableDriverCount
) {
}
