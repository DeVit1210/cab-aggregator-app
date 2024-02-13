package com.modsen.ride.service.feign;

import com.modsen.ride.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("PASSENGER-SERVICE")
public interface PassengerServiceClient {
    @GetMapping("/api/v1/passengers/{passengerId}")
    PassengerResponse findPassengerById(@PathVariable Long passengerId);
}
