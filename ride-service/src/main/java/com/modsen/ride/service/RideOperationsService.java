package com.modsen.ride.service;

import com.modsen.ride.dto.request.FinishRideRequest;
import com.modsen.ride.dto.response.RideResponse;

public interface RideOperationsService {
    RideResponse acceptRide(Long rideId);

    RideResponse dismissRide(Long rideId);

    RideResponse cancelRide(Long rideId);

    RideResponse notifyPassengerAboutWaiting(Long rideId);

    RideResponse startRide(Long rideId);

    RideResponse finishRide(FinishRideRequest request);
}
