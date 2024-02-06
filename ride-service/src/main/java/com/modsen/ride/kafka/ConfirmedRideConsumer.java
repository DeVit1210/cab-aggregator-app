package com.modsen.ride.kafka;

import com.modsen.ride.dto.request.ConfirmedRideRequest;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.model.Ride;
import com.modsen.ride.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmedRideConsumer {
    private final RideService rideService;

    @KafkaListener(topics = "${spring.kafka.ride-consumer-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRideWithDriver(ConfirmedRideRequest rideWithDriver) {
        Ride ride = rideService.findRideById(rideWithDriver.getRideId());
        ride.setDriverId(rideWithDriver.getDriverId());
        ride.setRideStatus(RideStatus.WAITING_FOR_DRIVER_CONFIRMATION);
        rideService.saveRide(ride);
    }
}
