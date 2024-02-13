package com.modsen.rating.service.feign;

import com.modsen.rating.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "DRIVER-SERVICE")
public interface DriverServiceClient {
    @GetMapping("api/v1/drivers/{driverId}")
    DriverResponse findDriverById(@PathVariable Long driverId);
}
