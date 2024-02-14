package com.modsen.ride.controller;

import com.modsen.ride.constants.ControllerMappings;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.service.RideOperationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerMappings.RIDE_OPERATIONS_CONTROLLER)
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

    @PatchMapping("/notify-arrival/{rideId}")
    public RideResponse notifyPassengerAboutArrival(@PathVariable Long rideId) {
        return rideOperationsService.notifyPassengerAboutArrival(rideId);
    }

    @PatchMapping("/finish/{rideId}")
    public RideResponse finishRide(@PathVariable Long rideId) {
        return rideOperationsService.finishRide(rideId);
    }

    @PatchMapping("/cancel/{rideId}")
    public RideResponse cancelRide(@PathVariable Long rideId) {
        return rideOperationsService.cancelRide(rideId);
    }
}
