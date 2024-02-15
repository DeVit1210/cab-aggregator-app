package com.modsen.ride.dto.response;

import java.util.Random;

public record DriverAvailabilityResponse(
        long totalDriverCount,
        long availableDriverCount
) {
    public static DriverAvailabilityResponse ofDefaults() {
        int driverCount = new Random().nextInt(100);
        return new DriverAvailabilityResponse(driverCount, driverCount);
    }
}
