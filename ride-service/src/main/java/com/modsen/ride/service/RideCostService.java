package com.modsen.ride.service;

import com.modsen.ride.dto.request.RideCostRequest;
import com.modsen.ride.dto.response.RideCostResponse;

public interface RideCostService {
    RideCostResponse calculateCost(RideCostRequest request);
}
