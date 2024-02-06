package com.modsen.driver.service;

import com.modsen.driver.dto.request.RideRequest;
import com.modsen.driver.model.Driver;

import java.util.List;

public interface DriverWithSuggestedRideService {
    List<Long> getDriverIdList(RideRequest request);

    Driver save(Driver driver, Long rideId);
}
