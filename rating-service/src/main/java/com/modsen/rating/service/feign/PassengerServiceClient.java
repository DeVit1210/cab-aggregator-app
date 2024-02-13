package com.modsen.rating.service.feign;

import com.modsen.rating.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PASSENGER-SERVICE")
public interface PassengerServiceClient {
    @GetMapping("/api/v1/passengers/{passengerId}")
    PassengerResponse findPassengerById(@PathVariable Long passengerId);
}
