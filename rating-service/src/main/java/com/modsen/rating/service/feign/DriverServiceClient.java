package com.modsen.rating.service.feign;

import com.modsen.rating.config.FeignConfig;
import com.modsen.rating.constants.ServiceMappings;
import com.modsen.rating.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = ServiceMappings.DRIVER_SERVICE,
        configuration = FeignConfig.class,
        path = ServiceMappings.DRIVER_BASE_URL
)
public interface DriverServiceClient {
    @GetMapping(ServiceMappings.DRIVER_BY_ID_URL)
    DriverResponse findDriverById(@PathVariable Long driverId);
}