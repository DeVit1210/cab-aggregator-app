package com.modsen.ride.service.feign;

import com.modsen.ride.dto.response.DriverAvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient("DRIVER-SERVICE")
public interface DriverServiceClient {
    @GetMapping("api/v1/drivers/availability")
    DriverAvailabilityResponse getDriverAvailability();
}
