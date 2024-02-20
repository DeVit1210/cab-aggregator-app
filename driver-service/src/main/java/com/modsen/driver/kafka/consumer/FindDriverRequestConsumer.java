package com.modsen.driver.kafka.consumer;

import com.modsen.driver.dto.request.FindDriverRequest;
import com.modsen.driver.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindDriverRequestConsumer {
    private final DriverService driverService;

    @KafkaListener(topics = "${spring.kafka.ride-consumer-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleFindDriverRequest(FindDriverRequest rideRequest) {
        driverService.handleFindDriverRequest(rideRequest);
    }
}
