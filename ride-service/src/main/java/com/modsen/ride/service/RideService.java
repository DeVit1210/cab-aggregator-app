package com.modsen.ride.service;

import com.modsen.ride.dto.request.PageSettingRequest;
import com.modsen.ride.dto.request.RideRequest;
import com.modsen.ride.dto.response.ConfirmedRideResponse;
import com.modsen.ride.dto.response.PagedRideResponse;
import com.modsen.ride.dto.response.RideListResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.enums.Role;
import com.modsen.ride.model.Ride;

public interface RideService {
    PagedRideResponse findRides(PageSettingRequest request);

    RideListResponse findAllRidesForPerson(Long personId, Role role);

    PagedRideResponse findRidesForPerson(Long personId, Role role, PageSettingRequest request);

    RideResponse createRide(RideRequest request);

    ConfirmedRideResponse findAvailableRideForDriver(Long driverId);

    ConfirmedRideResponse findConfirmedRideForPassenger(Long passengerId);

    Ride findRideById(Long rideId);

    RideResponse saveRide(Ride ride);
}
