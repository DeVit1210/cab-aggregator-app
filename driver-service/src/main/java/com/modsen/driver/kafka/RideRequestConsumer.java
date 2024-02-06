package com.modsen.driver.kafka;

import com.modsen.driver.dto.request.RideRequest;
import com.modsen.driver.dto.response.RideResponse;
import com.modsen.driver.mapper.RideResponseMapper;
import com.modsen.driver.service.DriverService;
import com.modsen.driver.service.DriverWithSuggestedRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideRequestConsumer {
    private final DriverService driverService;
    private final DriverWithSuggestedRideService driverWithSuggestedRideService;
    private final RideResponseProducer rideResponseProducer;
    private final RideResponseMapper rideResponseMapper;

    @KafkaListener(topics = "${spring.kafka.ride-consumer-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleRideRequest(RideRequest rideRequest) {
        RideResponse rideResponse = driverService.findAvailableDriverForRide(rideRequest)
                .map(driver -> driverWithSuggestedRideService.save(driver, rideRequest.getRideId()))
                .map(driver -> rideResponseMapper.toResponseWithDriver(rideRequest, driver.getId()))
                .orElse(rideResponseMapper.toResponseWithoutDriver(rideRequest));
        rideResponseProducer.sendRideResponse(rideResponse);
    }
}
