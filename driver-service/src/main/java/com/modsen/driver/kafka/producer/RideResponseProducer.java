package com.modsen.driver.kafka.producer;

import com.modsen.driver.dto.response.RideResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideResponseProducer {
    private final KafkaTemplate<String, RideResponse> kafkaTemplate;
    @Value("${spring.kafka.producer-topic.name}")
    private String producerTopicName;

    public void sendRideResponse(RideResponse rideResponse) {
        kafkaTemplate.send(producerTopicName, rideResponse);
    }
}
