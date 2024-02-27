package com.modsen.ride.service.feign;

import com.modsen.ride.config.FeignConfig;
import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.response.DriverAvailabilityResponse;
import com.modsen.ride.service.feign.fallback.DriverServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "${feign.client.driver.name}",
        configuration = FeignConfig.class,
        path = "${feign.client.driver.url}",
        fallback = DriverServiceClientFallback.class
)
public interface DriverServiceClient {
    @GetMapping(ServiceMappings.Url.DRIVER_AVAILABILITY_URL)
    DriverAvailabilityResponse getDriverAvailability();
}
