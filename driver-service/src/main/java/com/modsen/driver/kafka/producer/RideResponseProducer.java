package com.modsen.driver.kafka.producer;

import com.modsen.driver.dto.request.UpdateRideDriverRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideResponseProducer {
    private final KafkaTemplate<String, UpdateRideDriverRequest> kafkaTemplate;
    @Value("${spring.kafka.producer-topic.name}")
    private String producerTopicName;

    public void sendUpdateRideRequest(UpdateRideDriverRequest updateRideDriverRequest) {
        kafkaTemplate.send(producerTopicName, updateRideDriverRequest);
    }
}
