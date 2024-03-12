package com.modsen.e2e.client;

import com.modsen.e2e.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "${feign.client.driver.name}",
        path = "${feign.client.driver.path}"
)
public interface DriverServiceClient {
    @GetMapping("/{id}")
    DriverResponse findDriverById(@PathVariable Long id);
}

