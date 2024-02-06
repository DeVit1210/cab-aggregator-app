package com.modsen.ride.kafka;

import com.modsen.ride.dto.request.FindDriverRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideRequestProducer {
    private final KafkaTemplate<String, FindDriverRequest> kafkaTemplate;
    @Value("${spring.kafka.ride-producer-topic.name}")
    private String rideProducerTopicName;

    public void sendRequestForDriver(FindDriverRequest request) {
        kafkaTemplate.send(rideProducerTopicName, request);
    }
}