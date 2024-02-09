package com.modsen.ride.kafka.producer;

import com.modsen.ride.dto.request.ChangeDriverStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverStatusRequestProducer {
    private final KafkaTemplate<String, ChangeDriverStatusRequest> kafkaTemplate;
    @Value("${spring.kafka.status-producer-topic.name}")
    private String driverStatusProducerTopicName;

    public void changeDriverStatus(ChangeDriverStatusRequest request) {
        kafkaTemplate.send(driverStatusProducerTopicName, request);
    }
}
