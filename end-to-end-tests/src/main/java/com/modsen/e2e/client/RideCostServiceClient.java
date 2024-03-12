package com.modsen.e2e.client;

import com.modsen.e2e.dto.request.RideCostRequest;
import com.modsen.e2e.dto.response.RideCostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "${feign.client.ride.name}",
        contextId = "ride-cost",
        path = "${feign.client.ride.cost.path}"
)
public interface RideCostServiceClient {
    @PostMapping
    RideCostResponse calculateRideCost(@RequestBody RideCostRequest request);
}
