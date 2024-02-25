package com.modsen.ride.service;

import com.modsen.ride.dto.request.RideCostRequest;

public interface DistanceCalculator {
    double calculateDistance(RideCostRequest request);
}
