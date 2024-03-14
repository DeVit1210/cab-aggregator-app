package com.modsen.ride.service.feign;

import com.modsen.ride.config.FeignConfig;
import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.response.PassengerResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "${feign.client.passenger.name}",
        configuration = FeignConfig.class,
        path = "${feign.client.passenger.url}"
)
@CircuitBreaker(name = "passenger-circuit-breaker")
@Retry(name = "passenger-retry")
public interface PassengerServiceClient {
    @GetMapping(ServiceMappings.Url.PASSENGER_BY_ID_URL)
    PassengerResponse findPassengerById(@PathVariable Long passengerId);
}
