package com.modsen.promocode.service.feign;

import com.modsen.promocode.dto.response.RideListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("RIDE-SERVICE")
public interface RideServiceClient {
    @GetMapping
    RideListResponse findAllRidesForPerson(@RequestParam Long personId, @RequestParam String role);
}
