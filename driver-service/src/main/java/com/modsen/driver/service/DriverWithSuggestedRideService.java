package com.modsen.driver.service;

import com.modsen.driver.dto.request.RideRequest;

import java.util.List;

public interface DriverWithSuggestedRideService {
    List<Long> getDriverIdList(RideRequest request);
}
