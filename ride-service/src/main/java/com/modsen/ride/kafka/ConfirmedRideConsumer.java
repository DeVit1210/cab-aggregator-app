package com.modsen.ride.kafka;

import com.modsen.ride.dto.request.ConfirmedRideRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmedRideConsumer {
    @KafkaListener(topics = "${spring.kafka.ride-consumer-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRideWithDriver(ConfirmedRideRequest rideWithDriver) {

    }
}
