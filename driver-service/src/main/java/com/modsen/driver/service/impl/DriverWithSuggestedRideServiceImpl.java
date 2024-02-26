package com.modsen.driver.service.impl;

import com.modsen.driver.dto.request.FindDriverRequest;
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
    public List<Long> getDriverIdList(FindDriverRequest request) {
        return Optional.of(request.getRideId())
                .map(rideId -> suggestedRideRepository.findAllBySuggestedRideId(rideId)
                        .stream()
                        .map(DriverWithSuggestedRide::getDriver)
                        .map(Driver::getId)
                        .toList()
                )
                .orElse(Collections.emptyList());
    }

    @Override
    public void save(Driver driver, Long rideId) {
        DriverWithSuggestedRide driverWithSuggestedRide = DriverWithSuggestedRide.builder()
                .suggestedRideId(rideId)
                .driver(driver)
                .build();
        suggestedRideRepository.save(driverWithSuggestedRide);
    }
}
