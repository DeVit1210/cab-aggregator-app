package com.modsen.driver.service;

import com.modsen.driver.dto.request.FindDriverRequest;
import com.modsen.driver.model.Driver;

import java.util.List;

public interface DriverWithSuggestedRideService {
    List<Long> getDriverIdList(FindDriverRequest request);

    void save(Driver driver, Long rideId);
}
