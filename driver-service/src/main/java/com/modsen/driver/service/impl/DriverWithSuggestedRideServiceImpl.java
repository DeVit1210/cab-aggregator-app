package com.modsen.driver.service.impl;

import com.modsen.driver.dto.request.RideRequest;
import com.modsen.driver.model.Driver;
import com.modsen.driver.model.DriverWithSuggestedRide;
import com.modsen.driver.repository.DriverWithSuggestedRideRepository;
import com.modsen.driver.service.DriverWithSuggestedRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverWithSuggestedRideServiceImpl implements DriverWithSuggestedRideService {
    private final DriverWithSuggestedRideRepository suggestedRideRepository;
    @Override
    public List<Long> getDriverIdList(RideRequest request) {
        Long rideId = request.getId();

        return suggestedRideRepository.findAllBySuggestedRideId(rideId)
                .stream()
                .map(DriverWithSuggestedRide::getDriver)
                .map(Driver::getId)
                .toList();
    }
}
