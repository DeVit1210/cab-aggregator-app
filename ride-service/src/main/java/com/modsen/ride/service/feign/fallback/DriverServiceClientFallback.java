package com.modsen.ride.service.feign.fallback;

import com.modsen.ride.dto.response.DriverAvailabilityResponse;
import com.modsen.ride.service.feign.DriverServiceClient;
import org.springframework.stereotype.Component;

@Component
public class DriverServiceClientFallback implements DriverServiceClient {
    @Override
    public DriverAvailabilityResponse getDriverAvailability() {
        return DriverAvailabilityResponse.ofDefaults();
    }
}
