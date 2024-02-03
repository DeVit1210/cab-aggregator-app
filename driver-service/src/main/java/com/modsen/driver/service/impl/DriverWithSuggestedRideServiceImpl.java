package com.modsen.driver.service.impl;

import com.modsen.driver.dto.request.RideRequest;
import com.modsen.driver.model.Driver;
import com.modsen.driver.model.DriverWithSuggestedRide;
import com.modsen.driver.repository.DriverWithSuggestedRideRepository;
import com.modsen.driver.service.DriverWithSuggestedRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverWithSuggestedRideServiceImpl implements DriverWithSuggestedRideService {
    private final DriverWithSuggestedRideRepository suggestedRideRepository;

    @Override
    public List<Long> getDriverIdList(RideRequest request) {
        return Optional.of(request.getRideId())
                .map(rideId -> suggestedRideRepository.findAllBySuggestedRideId(rideId)
                        .stream()
                        .map(DriverWithSuggestedRide::getDriver)
                        .map(Driver::getId)
                        .toList()
                )
                .orElse(Collections.emptyList());
    }
}
