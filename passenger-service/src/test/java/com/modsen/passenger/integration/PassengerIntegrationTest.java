package com.modsen.passenger.integration;

import com.modsen.passenger.constants.MessageTemplates;
import com.modsen.passenger.constants.ServiceMappings;
import com.modsen.passenger.constants.TestConstants;
import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.AverageRatingListResponse;
import com.modsen.passenger.dto.response.AverageRatingResponse;
import com.modsen.passenger.dto.response.PassengerListResponse;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.exception.ApiExceptionInfo;
import com.modsen.passenger.model.Passenger;
import com.modsen.passenger.repository.PassengerRepository;
import com.modsen.passenger.service.feign.RatingServiceClient;
import com.modsen.passenger.utils.RestAssuredUtils;
import com.modsen.passenger.utils.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PassengerIntegrationTest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.2.0"));
    @MockBean
    public RatingServiceClient ratingServiceClient;
    @LocalServerPort
    private Integer port;
    @Autowired
    private PassengerRepository passengerRepository;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost";
        RestAssured.port = port;
        basePath = ServiceMappings.PASSENGER_CONTROLLER;
    }

    @AfterEach
    void tearDown() {
        passengerRepository.deleteAll();
    }

    @Test
    @Sql("classpath:insert-passengers-data.sql")
    void findAllPassengers_Success() {
        Mockito.when(ratingServiceClient.findAllAverageRatings(any()))
                .thenReturn(AverageRatingListResponse.empty());

        PassengerListResponse passengerListResponse = RestAssuredUtils.doGetAllResponse()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerListResponse.class);

        assertEquals(3, passengerListResponse.getQuantity());
    }

    @Test
    @Sql("classpath:insert-passengers-data.sql")
    void findPassengerById_ValidId_ShouldReturnPassenger() {
        Long passengerId = TestConstants.PASSENGER_ID;

        Mockito.when(ratingServiceClient.findAverageRating(anyLong(), any()))
                .thenReturn(AverageRatingResponse.empty(passengerId));

        RestAssuredUtils.doGetResponse(passengerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(passengerId.intValue()));

        assertEquals(3, passengerRepository.count());
    }

    @Test
    void findPassengerById_PassengerDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String exceptionMessage = String.format(MessageTemplates.PASSENGER_NOT_FOUND_BY_ID.getValue(), passengerId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        ApiExceptionInfo apiExceptionInfo = RestAssuredUtils.doGetResponse(passengerId)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(exceptionMessage, apiExceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, apiExceptionInfo.getHttpStatus());
    }


    @Test
    void createPassenger_ValidPassengerRequest_ShouldReturnCreatedPassenger() {
        PassengerRequest passengerRequest = TestUtils.defaultPassengerRequest();
        Passenger expectedPassenger = TestUtils.defaultPassenger();

        PassengerResponse passengerResponse = RestAssuredUtils.doPostResponse(passengerRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(PassengerResponse.class);

        Optional<Passenger> passenger = passengerRepository.findById(passengerResponse.id());

        assertTrue(passenger.isPresent());
        assertThat(passenger.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedPassenger);
    }

    @Test
    @Sql("classpath:insert-passengers-data.sql")
    void createPassenger_DuplicatePassengerEmail_ShouldReturnApiExceptionInfo() {
        PassengerRequest passengerRequest =
                TestUtils.passengerRequestWithPhoneNumber(TestConstants.PASSENGER_UPDATED_PHONE_NUMBER);
        String expectedExceptionMessage =
                String.format(MessageTemplates.EMAIL_NOT_UNIQUE.getValue(), TestConstants.PASSENGER_EMAIL);
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        ApiExceptionInfo exceptionInfo = RestAssuredUtils.doPostResponse(passengerRequest)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedExceptionMessage, exceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, exceptionInfo.getHttpStatus());
    }

    @Test
    @Sql("classpath:insert-passengers-data.sql")
    void createPassenger_DuplicatePassengerPhoneNumber_ShouldReturnApiExceptionInfo() {
        PassengerRequest passengerRequest = TestUtils.passengerRequestWithEmail(TestConstants.PASSENGER_UPDATED_EMAIL);
        String expectedExceptionMessage =
                String.format(MessageTemplates.PHONE_NUMBER_NOT_UNIQUE.getValue(), TestConstants.PASSENGER_PHONE_NUMBER);
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        ApiExceptionInfo exceptionInfo = RestAssuredUtils.doPostResponse(passengerRequest)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedExceptionMessage, exceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, exceptionInfo.getHttpStatus());
    }

    @Test
    @Sql("classpath:insert-passengers-data.sql")
    void updatePassenger_ValidIdAndPassengerRequest_ShouldReturnUpdatedPassenger() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String expectedNewEmail = TestConstants.PASSENGER_UPDATED_EMAIL;
        PassengerRequest passengerRequest = TestUtils.passengerRequestWithEmail(expectedNewEmail);

        Mockito.when(ratingServiceClient.findAverageRating(anyLong(), any()))
                .thenReturn(AverageRatingResponse.empty(passengerId));

        RestAssuredUtils.doPutResponse(passengerRequest, passengerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("email", equalTo(expectedNewEmail));
    }

    @Test
    @Sql("classpath:insert-passengers-data.sql")
    void updatePassenger_DuplicateEmailButForTheSamePassenger_ShouldReturnUpdatedPassenger() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String expectedNewPhoneNumber = TestConstants.PASSENGER_UPDATED_PHONE_NUMBER;
        PassengerRequest passengerRequest = TestUtils.passengerRequestWithPhoneNumber(expectedNewPhoneNumber);

        RestAssuredUtils.doPutResponse(passengerRequest, passengerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("phoneNumber", equalTo(expectedNewPhoneNumber));
    }

    @Test
    @Sql("classpath:insert-passengers-data.sql")
    void updatePassenger_DuplicateEmail_ShouldReturnApiExceptionInfo() {
        long passengerId = 2L;
        PassengerRequest passengerRequest = TestUtils.defaultPassengerRequest();
        String expectedExceptionMessage = String.format(MessageTemplates.EMAIL_NOT_UNIQUE.getValue(), TestConstants.PASSENGER_EMAIL);
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        ApiExceptionInfo exceptionInfo = given()
                .contentType(ContentType.JSON)
                .body(passengerRequest)
                .put("/" + passengerId)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedExceptionMessage, exceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, exceptionInfo.getHttpStatus());
    }

    @Test
    void updatePassenger_PassengerNotFoundById_ShouldReturnApiExceptionInfo() {
        Long passengerId = TestConstants.PASSENGER_ID;
        PassengerRequest passengerRequest = TestUtils.defaultPassengerRequest();
        String expectedExceptionMessage = String.format(MessageTemplates.PASSENGER_NOT_FOUND_BY_ID.getValue(), passengerId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        ApiExceptionInfo exceptionInfo = RestAssuredUtils.doPutResponse(passengerRequest, passengerId)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedExceptionMessage, exceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, exceptionInfo.getHttpStatus());
    }

    @Test
    @Sql("classpath:insert-passengers-data.sql")
    void deletePassenger_ValidPassengerId_ShouldDeletePassenger() {
        Long passengerId = TestConstants.PASSENGER_ID;

        RestAssuredUtils.doDeleteResponse(passengerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Optional<Passenger> passenger = passengerRepository.findById(passengerId);

        assertTrue(passenger.isEmpty());
    }

    @Test
    void deletePassenger_PassengerDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String expectedExceptionMessage = String.format(MessageTemplates.PASSENGER_NOT_FOUND_BY_ID.getValue(), passengerId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        ApiExceptionInfo exceptionInfo = RestAssuredUtils.doDeleteResponse(passengerId)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedExceptionMessage, exceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, exceptionInfo.getHttpStatus());
    }
}
