package com.modsen.e2e.client;

import com.modsen.e2e.dto.request.RideRequest;
import com.modsen.e2e.dto.response.RideListResponse;
import com.modsen.e2e.dto.response.RideResponse;
import com.modsen.e2e.dto.response.ShortRideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(
        name = "${feign.client.ride.name}",
        contextId = "ride-service",
        path = "${feign.client.ride.service.path}"
)
public interface RideServiceClient {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RideResponse createRide(@RequestBody RideRequest request);

    @GetMapping("/{rideId}")
    RideResponse findRideById(@PathVariable Long rideId);

    @GetMapping("/available/driver/{driverId}")
    ShortRideResponse findAvailableRideForDriver(@PathVariable Long driverId);

    @GetMapping("/confirmed/passenger/{passengerId}")
    ShortRideResponse findConfirmedRideForPassenger(@PathVariable Long passengerId);

    @GetMapping
    RideListResponse findAllRidesForPerson(@RequestParam Long personId, @RequestParam String role);
}
