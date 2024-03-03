package com.modsen.ride.integration;

import com.modsen.ride.constants.ExceptionConstants;
import com.modsen.ride.constants.MessageTemplates;
import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.FinishRideRequest;
import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import com.modsen.ride.dto.response.PaymentResponse;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.exception.base.BadRequestException;
import com.modsen.ride.repository.RideRepository;
import com.modsen.ride.service.feign.PaymentServiceClient;
import com.modsen.ride.service.feign.PromocodeServiceClient;
import com.modsen.ride.utils.RestAssuredUtils;
import com.modsen.ride.utils.TestUtils;
import io.restassured.response.Response;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "classpath:insert-rides-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RideOperationsControllerIntegrationTest extends BaseTestContainer {
    @Autowired
    private RideRepository rideRepository;
    @MockBean
    private PaymentServiceClient paymentServiceClient;
    @MockBean
    private PromocodeServiceClient promocodeServiceClient;
    @Autowired
    private KafkaConsumer<String, Object> kafkaConsumer;
    @Value("${spring.kafka.status-producer-topic.name}")
    private String driverStatusProducerTopicName;

    @AfterEach
    void tearDown() {
        rideRepository.deleteAll();
    }

    @Test
    void acceptRide_RideExistsAndHasValidCurrentState_ShouldReturnAcceptedRide() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long rideId = 4L;
        Response acceptRideResponse = RestAssuredUtils.acceptRideResponse(rideId);

        validateStatusCodeAndAssert(acceptRideResponse, rideId, RideStatus.PENDING);
        validateKafkaMessageSent(true);
    }

    @Test
    void acceptRide_InvalidCurrentRideState_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long invalidRideId = 1L;
        String expectedExceptionMessage = ExceptionConstants.WAITING_FOR_CONFIRMATION_STATE_REQUIRED;
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response acceptRideResponse = RestAssuredUtils.acceptRideResponse(invalidRideId);

        extractApiExceptionInfoAndAssert(acceptRideResponse, expectedHttpStatus, expectedExceptionMessage);
        validateKafkaMessageSent(false);
    }

    @Test
    void dismissRide_RideExistsAndHasValidCurrentState_ShouldReturnDismissedRide() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long rideId = 4L;
        Response dismissRideResponse = RestAssuredUtils.dismissRideResponse(rideId);

        validateStatusCodeAndAssert(dismissRideResponse, rideId, RideStatus.WITHOUT_DRIVER);
        validateKafkaMessageSent(true);
    }

    @Test
    void dismissRide_InvalidCurrentRideState_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long invalidRideId = 1L;
        String expectedExceptionMessage = ExceptionConstants.WAITING_FOR_CONFIRMATION_STATE_REQUIRED;
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response acceptRideResponse = RestAssuredUtils.dismissRideResponse(invalidRideId);

        extractApiExceptionInfoAndAssert(acceptRideResponse, expectedHttpStatus, expectedExceptionMessage);
        validateKafkaMessageSent(false);
    }

    @Test
    void notifyPassengerAboutWaiting_RideExistsAndHasValidCurrentState_ShouldReturnRide() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long rideId = 6L;
        Response notifyPassengerResponse = RestAssuredUtils.notifyPassengerResponse(rideId);

        validateStatusCodeAndAssert(notifyPassengerResponse, rideId, RideStatus.PENDING);
        validateKafkaMessageSent(true);
    }

    @Test
    void notifyPassengerAboutWaiting_InvalidCurrentRideStatus_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long invalidRideId = TestConstants.RIDE_ID;
        String expectedExceptionMessage = ExceptionConstants.PENDING_STATUS_REQUIRED;
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response notifyPassengerResponse = RestAssuredUtils.notifyPassengerResponse(invalidRideId);

        extractApiExceptionInfoAndAssert(notifyPassengerResponse, expectedHttpStatus, expectedExceptionMessage);
        validateKafkaMessageSent(false);
    }

    @Test
    void startRide_RideExistsAndHasValidCurrenState_ShouldReturnStartedRide() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long rideId = 6L;
        Response startRideResponse = RestAssuredUtils.startRideResponse(rideId);

        validateStatusCodeAndAssert(startRideResponse, rideId, RideStatus.ACTIVE);
        validateKafkaMessageSent(true);
    }

    @Test
    void startRide_InvalidCurrentRideState_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long invalidRideId = TestConstants.RIDE_ID;
        String expectedExceptionMessage = ExceptionConstants.PENDING_STATUS_REQUIRED;
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response startRideResponse = RestAssuredUtils.startRideResponse(invalidRideId);

        extractApiExceptionInfoAndAssert(startRideResponse, expectedHttpStatus, expectedExceptionMessage);
        validateKafkaMessageSent(false);
    }

    @ParameterizedTest
    @ValueSource(longs = {4L, 5L, 6L})
    void cancelRide_RideExistsAndHasValidCurrentState_ShouldReturnCanceledRide(Long validRideId) {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Response cancelRideResponse = RestAssuredUtils.cancelRideResponse(validRideId);

        validateStatusCodeAndAssert(cancelRideResponse, validRideId, RideStatus.CANCELED);
        validateKafkaMessageSent(true);
    }

    @Test
    void cancelRide_InvalidCurrentRideState_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long invalidRideId = 2L;
        String expectedExceptionMessage = ExceptionConstants.PENDING_STATUS_REQUIRED;
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;


        Response startRideResponse = RestAssuredUtils.startRideResponse(invalidRideId);

        extractApiExceptionInfoAndAssert(startRideResponse, expectedHttpStatus, expectedExceptionMessage);
        validateKafkaMessageSent(false);
    }

    @Test
    void finishRide_ValidFinishRideRequest_ShouldReturnFinishedRide() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long rideId = 2L;
        FinishRideRequest finishRideRequest = TestUtils.finishRideRequestWithId(rideId);

        Mockito.when(paymentServiceClient.createPayment(ArgumentMatchers.any()))
                .thenReturn(PaymentResponse.builder().build());
        Mockito.when(promocodeServiceClient.findNotConfirmedPromocode(ArgumentMatchers.any()))
                .thenReturn(AppliedPromocodeResponse.empty());

        Response finishRideResponse = RestAssuredUtils.finishRideResponse(finishRideRequest);

        validateStatusCodeAndAssert(finishRideResponse, rideId, RideStatus.FINISHED);
        validateKafkaMessageSent(true);
    }

    @Test
    void finishRide_RideDoesNotExist_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long nonExistentRideId = 99L;
        FinishRideRequest finishRideRequest = TestUtils.finishRideRequestWithId(nonExistentRideId);
        String expectedExceptionMessage = String.format(MessageTemplates.RIDE_NOT_FOUND_BY_ID.getValue(), nonExistentRideId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response finishRideResponse = RestAssuredUtils.finishRideResponse(finishRideRequest);

        extractApiExceptionInfoAndAssert(finishRideResponse, expectedHttpStatus, expectedExceptionMessage);
        validateKafkaMessageSent(false);
    }

    @Test
    void finishRide_PaymentCannotBeProcessed_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singleton(driverStatusProducerTopicName));
        Long rideId = 2L;
        FinishRideRequest finishRideRequest = TestUtils.finishRideRequestWithId(rideId);
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Mockito.when(paymentServiceClient.createPayment(ArgumentMatchers.any()))
                .thenThrow(BadRequestException.class);

        RestAssuredUtils.finishRideResponse(finishRideRequest)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value());
        validateKafkaMessageSent(false);
    }


    private void validateStatusCodeAndAssert(Response response, Long rideId, RideStatus expectedRideStatus) {
        response.then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.ID_FIELD, equalTo(rideId.intValue()))
                .body(TestConstants.FieldNames.RIDE_STATUS_FIELD, equalTo(expectedRideStatus.name()));
    }

    private void validateKafkaMessageSent(boolean isMessageSent) {
        ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(100));
        assertEquals(isMessageSent, records.count() == 1);
    }
}
