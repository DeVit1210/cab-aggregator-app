package com.modsen.ride.kafka.consumer;

import com.modsen.ride.dto.request.UpdateRideDriverRequest;
import com.modsen.ride.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateRideDriverRequestConsumer {
    private final RideService rideService;

    @KafkaListener(topics = "${spring.kafka.ride-consumer-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRideWithDriver(UpdateRideDriverRequest request) {
        rideService.handleUpdateDriver(request);
    }
}
