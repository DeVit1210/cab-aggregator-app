package com.modsen.driver.kafka;

import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverStatusConsumer {
    private final DriverService driverService;
    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.status-consumer-topic.name}")
    public void handleChangeDriverStatusRequest(ChangeDriverStatusRequest request) {
        driverService.updateDriverStatus(request);
    }
}
