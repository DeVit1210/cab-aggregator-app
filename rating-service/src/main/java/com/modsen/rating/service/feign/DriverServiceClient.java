package com.modsen.rating.service.feign;

import com.modsen.rating.config.FeignConfig;
import com.modsen.rating.constants.ServiceMappings;
import com.modsen.rating.dto.response.DriverResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = ServiceMappings.DRIVER_SERVICE,
        configuration = FeignConfig.class,
        path = ServiceMappings.DRIVER_BASE_URL
)
@CircuitBreaker(name = "driver-circuit-breaker")
@Retry(name = "driver-retry")
public interface DriverServiceClient {
    @GetMapping(ServiceMappings.DRIVER_BY_ID_URL)
    DriverResponse findDriverById(@PathVariable Long driverId);
}