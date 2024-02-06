package com.modsen.ride.service;

import com.modsen.ride.dto.response.RideResponse;

public interface RideOperationsService {
    RideResponse acceptRide(Long rideId);

    void dismissRide(Long rideId);

    RideResponse notifyPassengerAboutWaiting(Long rideId);

    RideResponse startRide(Long rideId);

    RideResponse finishRide(Long rideId);

    RideResponse cancelRide(Long rideId);
}
