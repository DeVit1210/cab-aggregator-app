package com.modsen.e2e.client;

import com.modsen.e2e.dto.request.FinishRideRequest;
import com.modsen.e2e.dto.response.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "${feign.client.ride.name}",
        contextId = "ride-operations",
        path = "${feign.client.ride.operations.path}"
)
public interface RideOperationsServiceClient {
    @PutMapping("/accept/{rideId}")
    RideResponse acceptRide(@PathVariable Long rideId);

    @PutMapping("/dismiss/{rideId}")
    RideResponse dismissRide(@PathVariable Long rideId);

    @PutMapping("/start/{rideId}")
    RideResponse startRide(@PathVariable Long rideId);

    @PutMapping("/finish")
    RideResponse finishRide(@RequestBody FinishRideRequest request);
}
