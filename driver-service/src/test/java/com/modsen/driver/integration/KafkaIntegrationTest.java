package com.modsen.driver.integration;

import com.modsen.driver.constants.TestConstants;
import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.FindDriverRequest;
import com.modsen.driver.dto.request.UpdateRideDriverRequest;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.kafka.consumer.DriverStatusConsumer;
import com.modsen.driver.kafka.consumer.FindDriverRequestConsumer;
import com.modsen.driver.model.Driver;
import com.modsen.driver.repository.DriverRepository;
import com.modsen.driver.utils.TestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "classpath:insert-drivers-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class KafkaIntegrationTest extends BaseTestContainer {
    @Autowired
    private DriverStatusConsumer driverStatusConsumer;
    @Autowired
    private FindDriverRequestConsumer findDriverRequestConsumer;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private KafkaConsumer<String, UpdateRideDriverRequest> kafkaConsumer;
    @Autowired
    private DriverRepository driverRepository;
    @Value("${spring.kafka.status-consumer-topic.name}")
    private String driverStatusConsumerTopic;
    @Value("${spring.kafka.ride-consumer-topic.name}")
    private String rideConsumerTopicName;
    @Value("${spring.kafka.producer-topic.name}")
    private String producerTopicName;

    @Test
    void handleChangeDriverStatusRequest_Success() {
        ChangeDriverStatusRequest request = TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.AVAILABLE);
        kafkaTemplate.send(driverStatusConsumerTopic, request);

        driverStatusConsumer.handleChangeDriverStatusRequest(request);

        Optional<Driver> driver = driverRepository.findById(request.getDriverId());

        assertTrue(driver.isPresent());
        assertEquals(request.getDriverStatus(), driver.get().getDriverStatus());
    }

    @Test
    void handleFindDriverRequestTopic_AvailableDriverExists_ShouldReturnAvailableDriver() {
        Long firstAvailableDriverId = 2L;
        kafkaConsumer.subscribe(Collections.singletonList(producerTopicName));

        FindDriverRequest request = new FindDriverRequest(TestConstants.RIDE_ID);
        kafkaTemplate.send(rideConsumerTopicName, request);

        findDriverRequestConsumer.handleFindDriverRequest(request);

        ConsumerRecords<String, UpdateRideDriverRequest> records = kafkaConsumer.poll(Duration.ofMillis(100));
        ConsumerRecord<String, UpdateRideDriverRequest> actualRecord = records.iterator().next();
        UpdateRideDriverRequest updateRideDriverRequest = actualRecord.value();

        assertEquals(1, records.count());
        assertTrue(updateRideDriverRequest.isDriverAvailable());
        assertEquals(firstAvailableDriverId, updateRideDriverRequest.getDriverId());
    }

    @Test
    @Sql(value = "classpath:insert-driver-with-suggested-rides-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:delete-driver-with-suggested-rides-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void handleFindDriverRequestTopic_OneDriverAlreadyDismissedButAnotherAvailable_ShouldReturnAvailableDriver() {
        Long availableButAlreadyDismissedRideDriverId = 2L;
        Long firstAvailableDriverId = 6L;
        kafkaConsumer.subscribe(Collections.singletonList(producerTopicName));

        FindDriverRequest request = new FindDriverRequest(TestConstants.RIDE_ID);
        kafkaTemplate.send(rideConsumerTopicName, request);

        findDriverRequestConsumer.handleFindDriverRequest(request);

        ConsumerRecords<String, UpdateRideDriverRequest> records = kafkaConsumer.poll(Duration.ofMillis(100));
        ConsumerRecord<String, UpdateRideDriverRequest> actualRecord = records.iterator().next();
        UpdateRideDriverRequest updateRideDriverRequest = actualRecord.value();

        assertEquals(1, records.count());
        assertTrue(updateRideDriverRequest.isDriverAvailable());
        assertNotEquals(availableButAlreadyDismissedRideDriverId, updateRideDriverRequest.getDriverId());
        assertEquals(firstAvailableDriverId, updateRideDriverRequest.getDriverId());
    }

    @Test
    void handleFindDriverRequestTopic_NoAvailableDrivers_ShouldReturnResponseWithoutDriver() {
        driverRepository.deleteAll();
        kafkaConsumer.subscribe(Collections.singletonList(producerTopicName));

        FindDriverRequest request = new FindDriverRequest(TestConstants.RIDE_ID);
        kafkaTemplate.send(rideConsumerTopicName, request);

        findDriverRequestConsumer.handleFindDriverRequest(request);

        ConsumerRecords<String, UpdateRideDriverRequest> records = kafkaConsumer.poll(Duration.ofMillis(100));
        ConsumerRecord<String, UpdateRideDriverRequest> actualRecord = records.iterator().next();
        UpdateRideDriverRequest updateRideDriverRequest = actualRecord.value();

        assertEquals(1, records.count());
        assertFalse(updateRideDriverRequest.isDriverAvailable());
        assertNull(updateRideDriverRequest.getDriverId());
    }
}
