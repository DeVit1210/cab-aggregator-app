package com.modsen.ride.kafka;

import com.modsen.ride.dto.RideWithDriverDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideWithDriverConsumer {
    @KafkaListener(topics = "${spring.kafka.ride-consumer-topic.name}", groupId = "${spring.kafka.consumer.groupId}")
    public void consumeRideWithDriver(RideWithDriverDto rideWithDriver) {

    }
}
