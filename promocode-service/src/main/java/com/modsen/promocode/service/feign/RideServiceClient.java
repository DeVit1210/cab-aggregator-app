package com.modsen.promocode.service.feign;

import com.modsen.promocode.config.FeignConfig;
import com.modsen.promocode.constants.ServiceMappings;
import com.modsen.promocode.dto.response.RideListResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = ServiceMappings.RIDE_SERVICE,
        configuration = FeignConfig.class,
        path = ServiceMappings.RIDE_BASE_URL
)
@CircuitBreaker(name = "ride-circuit-breaker")
@Retry(name = "ride-retry")
public interface RideServiceClient {
    @GetMapping
    RideListResponse findAllRidesForPerson(@RequestParam Long personId, @RequestParam String role);
}
