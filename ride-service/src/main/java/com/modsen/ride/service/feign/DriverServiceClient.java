package com.modsen.ride.service.feign;

import com.modsen.ride.config.FeignConfig;
import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.response.DriverAvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = ServiceMappings.ServiceName.DRIVER_SERVICE,
        configuration = FeignConfig.class,
        url = ServiceMappings.BaseUrl.DRIVER_SERVICE
)
public interface DriverServiceClient {
    @GetMapping(ServiceMappings.Url.DRIVER_AVAILABILITY_URL)
    DriverAvailabilityResponse getDriverAvailability();
}
