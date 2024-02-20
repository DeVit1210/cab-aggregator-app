package com.modsen.ride.controller;

import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.request.FinishRideRequest;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.service.RideOperationsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ServiceMappings.RIDE_OPERATIONS_CONTROLLER)
@RequiredArgsConstructor
public class RideOperationsController {
    private final RideOperationsService rideOperationsService;

    @PatchMapping("/accept/{rideId}")
    public RideResponse acceptRide(@PathVariable Long rideId) {
        return rideOperationsService.acceptRide(rideId);
    }

    @PatchMapping("/dismiss/{rideId}")
    public RideResponse dismissRide(@PathVariable Long rideId) {
        return rideOperationsService.dismissRide(rideId);
    }

    @PatchMapping("/notify-waiting/{rideId}")
    public RideResponse notifyPassengerAboutWaiting(@PathVariable Long rideId) {
        return rideOperationsService.notifyPassengerAboutWaiting(rideId);
    }

    @PatchMapping("/start/{rideId}")
    public RideResponse startRide(@PathVariable Long rideId) {
        return rideOperationsService.startRide(rideId);
    }

    @PatchMapping("/finish")
    public RideResponse finishRide(@Valid @RequestBody FinishRideRequest request) {
        return rideOperationsService.finishRide(request);
    }

    @PatchMapping("/cancel/{rideId}")
    public RideResponse cancelRide(@PathVariable Long rideId) {
        return rideOperationsService.cancelRide(rideId);
    }
}
