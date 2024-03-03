package com.modsen.ride.integration;

import com.modsen.ride.constants.MessageTemplates;
import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.RideRequest;
import com.modsen.ride.dto.response.CreditCardResponse;
import com.modsen.ride.dto.response.PassengerResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.dto.response.StripeCustomerResponse;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.enums.Role;
import com.modsen.ride.exception.base.NotFoundException;
import com.modsen.ride.model.Ride;
import com.modsen.ride.repository.RideRepository;
import com.modsen.ride.service.feign.PassengerServiceClient;
import com.modsen.ride.service.feign.PaymentServiceClient;
import com.modsen.ride.utils.RestAssuredUtils;
import com.modsen.ride.utils.TestUtils;
import io.restassured.response.Response;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RideControllerIntegrationTest extends BaseTestContainer {
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private KafkaConsumer<String, Object> kafkaConsumer;
    @MockBean
    private PassengerServiceClient passengerServiceClient;
    @MockBean
    private PaymentServiceClient paymentServiceClient;
    @Value("${spring.kafka.ride-producer-topic.name}")
    private String rideProducerTopicName;


    static Stream<Arguments> findAlLRidesForPersonArgumentsProvider() {
        return Stream.of(
                Arguments.of(1L, Role.DRIVER.name(), 2),
                Arguments.of(1L, Role.PASSENGER.name(), 1),
                Arguments.of(3L, Role.PASSENGER.name(), 0)
        );
    }

    @AfterEach
    void tearDown() {
        rideRepository.deleteAll();
    }

    @Test
    void createRide_ValidRideRequest_ShouldReturnCreatedRide() {
        kafkaConsumer.subscribe(Collections.singletonList(rideProducerTopicName));
        RideRequest request = TestUtils.defaultRideRequest();
        RideResponse expectedRideResponse = TestUtils.defaultRideResponse();
        setupMocksForValidResponses();

        RideResponse rideResponse = RestAssuredUtils.createRideResponse(request)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(RideResponse.class);

        validateKafkaMessageSent(true);
        assertEquals(expectedRideResponse.passengerId(), rideResponse.passengerId());
        assertEquals(expectedRideResponse.rideCost(), rideResponse.rideCost());
        assertEquals(RideStatus.WITHOUT_DRIVER, rideResponse.rideStatus());
        assertNull(rideResponse.driverId());
    }

    @Test
    @Sql("classpath:insert-rides-data.sql")
    void createRide_PassengerDoesNotExist_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singletonList(rideProducerTopicName));
        RideRequest request = TestUtils.defaultRideRequest();
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Mockito.when(passengerServiceClient.findPassengerById(anyLong()))
                .thenThrow(NotFoundException.class);

        RestAssuredUtils.createRideResponse(request)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value());

        validateKafkaMessageSent(false);
    }

    @Test
    @Sql("classpath:insert-rides-data.sql")
    void createRide_NotFinishedRideAlreadyExists_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singletonList(rideProducerTopicName));
        RideRequest request = TestUtils.defaultRideRequest();
        String expectedExceptionMessage = String.format(
                MessageTemplates.NOT_FINISHED_RIDE_EXISTS_FOR_PASSENGER.getValue(),
                request.getPassengerId()
        );
        HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;
        setupMocksForValidResponses();

        Response createRideResponse = RestAssuredUtils.createRideResponse(request);

        extractApiExceptionInfoAndAssert(createRideResponse, expectedHttpStatus, expectedExceptionMessage);
        validateKafkaMessageSent(false);
    }

    @Test
    void createRide_StripeCustomerDoesNotExist_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singletonList(rideProducerTopicName));
        RideRequest request = TestUtils.defaultRideRequest();
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Mockito.when(paymentServiceClient.findStripeCustomerById(anyLong()))
                .thenThrow(NotFoundException.class);

        RestAssuredUtils.createRideResponse(request)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value());

        validateKafkaMessageSent(false);
    }

    @Test
    void createRide_NoDefaultCreditCardForPassenger_ShouldReturnApiExceptionInfo() {
        kafkaConsumer.subscribe(Collections.singletonList(rideProducerTopicName));
        RideRequest request = TestUtils.defaultRideRequest();
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Mockito.when(paymentServiceClient.getDefaultCreditCard(anyLong()))
                .thenThrow(NotFoundException.class);

        RestAssuredUtils.createRideResponse(request)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value());

        validateKafkaMessageSent(false);
    }

    @Test
    @Sql("classpath:insert-rides-data.sql")
    void findRideById_RideExists_ShouldReturnRide() {
        Long rideId = TestConstants.RIDE_ID;
        Ride expectedRide = TestUtils.defaultRide();

        RideResponse rideResponse = RestAssuredUtils.findRideByIdResponse(rideId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);

        Optional<Ride> foundRide = rideRepository.findById(rideResponse.id());
        assertTrue(foundRide.isPresent());
        assertEquals(foundRide.get(), expectedRide);
    }

    @Test
    void findRideById_RideDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long rideId = TestConstants.RIDE_ID;
        String expectedExceptionMessage = String.format(MessageTemplates.RIDE_NOT_FOUND_BY_ID.getValue(), rideId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response findRideByIdResponse = RestAssuredUtils.findRideByIdResponse(rideId);

        extractApiExceptionInfoAndAssert(findRideByIdResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @ParameterizedTest
    @MethodSource("findAlLRidesForPersonArgumentsProvider")
    @Sql("classpath:insert-rides-data.sql")
    void findAllRidesForPerson_Success(Long personId, String role, int expectedRideQuantity) {
        RestAssuredUtils.findAllRidesForPerson(personId, role)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.RIDES_FIELD, iterableWithSize(expectedRideQuantity))
                .body(TestConstants.FieldNames.QUANTITY_FIELD, equalTo(expectedRideQuantity));
    }

    @Test
    @Sql("classpath:insert-rides-data.sql")
    void findAvailableRideForDriver_RideExists_ShouldReturnAvailableRide() {
        Long driverId = 2L;

        RestAssuredUtils.findAvailableRideForDriver(driverId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.DRIVER_ID_FIELD, equalTo(driverId.intValue()))
                .body(TestConstants.FieldNames.RIDE_STATUS_FIELD, equalTo(RideStatus.WAITING_FOR_DRIVER_CONFIRMATION.name()));
    }

    @Test
    @Sql("classpath:insert-rides-data.sql")
    void findAvailableRideForDriver_RideDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long driverId = TestConstants.DRIVER_ID;
        String expectedExceptionMessage = String.format(MessageTemplates.NO_AVAILABLE_RIDE_FOR_DRIVER.getValue(), driverId);
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response findAvailableRideForDriverResponse = RestAssuredUtils.findAvailableRideForDriver(driverId);

        extractApiExceptionInfoAndAssert(findAvailableRideForDriverResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-rides-data.sql")
    void findConfirmedRideForPassenger_RideExists_ShouldReturnConfirmedRide() {
        Long passengerId = TestConstants.PASSENGER_ID;

        RestAssuredUtils.findConfirmedRideForPassenger(passengerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.PASSENGER_ID_FIELD, equalTo(passengerId.intValue()))
                .body(TestConstants.FieldNames.RIDE_STATUS_FIELD, oneOf(RideStatus.PENDING.name(), RideStatus.ACTIVE.name()));
    }

    @Test
    @Sql("classpath:insert-rides-data.sql")
    void findConfirmedRideForPassenger_RideDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long passengerId = 2L;
        String expectedExceptionMessage = String.format(MessageTemplates.NO_ACTIVE_RIDE_FOR_USER.getValue(), passengerId);
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response findConfirmedRideForPassengerResponse = RestAssuredUtils.findConfirmedRideForPassenger(passengerId);

        extractApiExceptionInfoAndAssert(findConfirmedRideForPassengerResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    private void setupMocksForValidResponses() {
        Mockito.when(passengerServiceClient.findPassengerById(anyLong()))
                .thenReturn(PassengerResponse.builder().build());
        Mockito.when(paymentServiceClient.findStripeCustomerById(anyLong()))
                .thenReturn(StripeCustomerResponse.builder().build());
        Mockito.when(paymentServiceClient.getDefaultCreditCard(anyLong()))
                .thenReturn(CreditCardResponse.builder().build());
    }

    private void validateKafkaMessageSent(boolean isMessageSent) {
        ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMillis(100));
        assertEquals(isMessageSent, records.count() == 1);
    }
}
