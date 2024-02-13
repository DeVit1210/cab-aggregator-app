package com.modsen.rating.service.feign;

import com.modsen.rating.dto.response.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RIDE-SERVICE")
public interface RideServiceClient {
    @GetMapping("/api/v1/rides/{rideId}")
    RideResponse findRideById(@PathVariable Long rideId);
}
