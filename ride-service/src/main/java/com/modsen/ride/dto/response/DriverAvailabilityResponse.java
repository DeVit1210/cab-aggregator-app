package com.modsen.ride.dto.response;

public record DriverAvailabilityResponse(
        long totalDriverCount,
        long availableDriverCount
) {
    public static DriverAvailabilityResponse ofDefaults() {
        return new DriverAvailabilityResponse(0, 0);
    }
}
