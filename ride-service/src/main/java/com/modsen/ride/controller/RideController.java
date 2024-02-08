package com.modsen.ride.controller;

import com.modsen.ride.constants.ControllerMappings;
import com.modsen.ride.dto.request.PageSettingRequest;
import com.modsen.ride.dto.request.RideRequest;
import com.modsen.ride.dto.response.ConfirmedRideResponse;
import com.modsen.ride.dto.response.PagedRideResponse;
import com.modsen.ride.dto.response.RideListResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.enums.Role;
import com.modsen.ride.service.RideService;
import com.modsen.ride.validation.EnumValue;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerMappings.RIDE_CONTROLLER)
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponse createRide(@Valid @RequestBody RideRequest request) {
        return rideService.createRide(request);
    }

    @GetMapping("/page")
    public PagedRideResponse findRides(PageSettingRequest request) {
        return rideService.findRides(request);
    }

    @GetMapping("/page/{personId}")
    public PagedRideResponse findRidesForPerson(@PathVariable Long personId,
                                                @RequestParam @EnumValue(enumClass = Role.class) String role,
                                                PageSettingRequest request) {
        return rideService.findRidesForPerson(personId, Role.valueOf(role), request);
    }

    @GetMapping
    public RideListResponse findAllRidesForPerson(@RequestParam Long personId,
                                                  @RequestParam @EnumValue(enumClass = Role.class) String role) {
        return rideService.findAllRidesForPerson(personId, Role.valueOf(role));
    }

    @GetMapping("/available/driver/{driverId}")
    public ConfirmedRideResponse findAvailableRideForDriver(@PathVariable Long driverId) {
        return rideService.findAvailableRideForDriver(driverId);
    }

    @GetMapping("/confirmed/passenger/{passengerId}")
    public ConfirmedRideResponse findConfirmedRideForPassenger(@PathVariable Long passengerId) {
        return rideService.findConfirmedRideForPassenger(passengerId);
    }
}
