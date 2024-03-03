package com.modsen.driver.integration;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.constants.TestConstants;
import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.response.AverageRatingListResponse;
import com.modsen.driver.dto.response.AverageRatingResponse;
import com.modsen.driver.dto.response.DriverAccountResponse;
import com.modsen.driver.dto.response.DriverAvailabilityResponse;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.model.Driver;
import com.modsen.driver.repository.DriverRepository;
import com.modsen.driver.service.feign.PaymentServiceClient;
import com.modsen.driver.service.feign.RatingServiceClient;
import com.modsen.driver.utils.RestAssuredUtils;
import com.modsen.driver.utils.TestUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DriverControllerIntegrationTest extends BaseTestContainer {
    @Autowired
    private DriverRepository driverRepository;
    @MockBean
    private PaymentServiceClient paymentServiceClient;
    @MockBean
    private RatingServiceClient ratingServiceClient;

    static Stream<Arguments> changeDriverStatusArgumentsProvider() {
        return Stream.of(
                Arguments.of(
                        ChangeDriverStatusRequest.builder()
                                .driverStatus(DriverStatus.AVAILABLE)
                                .driverId(5L)
                                .build()
                ),
                Arguments.of(
                        ChangeDriverStatusRequest.builder()
                                .driverStatus(DriverStatus.OFFLINE)
                                .driverId(2L)
                                .build()
                )
        );
    }

    @AfterEach
    void tearDown() {
        driverRepository.deleteAll();
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void findAllDrivers_Success() {
        int expectedSize = 5;

        Mockito.when(ratingServiceClient.findAllAverageRatings(any()))
                .thenReturn(AverageRatingListResponse.empty());

        RestAssuredUtils.findAllDriversResponse()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.DRIVERS_FIELD, iterableWithSize(expectedSize))
                .body(TestConstants.FieldNames.QUANTITY_FIELD, equalTo(expectedSize));
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void findDriverById_DriverExists_ShouldReturnDriver() {
        Long driverId = TestConstants.DRIVER_ID;
        Driver expectedDriver = TestUtils.driverWithStatus(DriverStatus.AVAILABLE);

        Mockito.when(ratingServiceClient.findAverageRating(anyLong(), anyString()))
                .thenReturn(AverageRatingResponse.empty(driverId));

        DriverResponse driverResponse = RestAssuredUtils.findDriverByIdResponse(driverId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);

        assertEquals(expectedDriver.getId(), driverResponse.id());
        assertEquals(expectedDriver.getEmail(), driverResponse.email());
        assertEquals(expectedDriver.getPhoneNumber(), driverResponse.phoneNumber());
        assertEquals(DriverStatus.OFFLINE, driverResponse.driverStatus());
    }

    @Test
    void findDriverById_DriverDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long driverId = TestConstants.DRIVER_ID;
        String expectedExceptionMessage = String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_ID.getValue(), driverId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response findDriverByIdResponse = RestAssuredUtils.findDriverByIdResponse(driverId);

        extractApiExceptionInfoAndAssert(findDriverByIdResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    void createDriver_ValidDriverRequest_ShouldReturnCreatedDriver() {
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        Driver expectedDriver = TestUtils.defaultDriver();

        DriverResponse driverResponse = RestAssuredUtils.createDriverResponse(driverRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(DriverResponse.class);

        Optional<Driver> createdDriver = driverRepository.findById(driverResponse.id());

        assertTrue(createdDriver.isPresent());
        assertThat(createdDriver.get())
                .usingRecursiveComparison()
                .ignoringFields(TestConstants.FieldNames.ID_FIELD)
                .isEqualTo(expectedDriver);
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void createDriver_DuplicateEmail_ShouldReturnApiExceptionInfo() {
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        String expectedExceptionMessage = String.format(
                MessageTemplates.EMAIL_NOT_UNIQUE.getValue(),
                driverRequest.getEmail()
        );
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response createDriverResponse = RestAssuredUtils.createDriverResponse(driverRequest);

        extractApiExceptionInfoAndAssert(createDriverResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void createDriver_DuplicatePhoneNumber_ShouldReturnApiExceptionInfo() {
        String validEmail = "valid@gmail.com";
        DriverRequest driverRequest = TestUtils.driverRequestWithEmail(validEmail);
        String expectedExceptionMessage = String.format(
                MessageTemplates.PHONE_NUMBER_NOT_UNIQUE.getValue(),
                driverRequest.getPhoneNumber()
        );
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response createDriverResponse = RestAssuredUtils.createDriverResponse(driverRequest);

        extractApiExceptionInfoAndAssert(createDriverResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void updateDriver_ValidIdAndDriverRequest_ShouldReturnUpdatedDriver() {
        Long driverId = TestConstants.DRIVER_ID;
        String expectedNewEmail = TestConstants.DRIVER_UPDATED_EMAIL;
        DriverRequest driverRequest = TestUtils.driverRequestWithEmail(expectedNewEmail);

        Mockito.when(ratingServiceClient.findAverageRating(anyLong(), anyString()))
                .thenReturn(AverageRatingResponse.empty(driverId));

        RestAssuredUtils.updateDriverResponse(driverId, driverRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.ID_FIELD, equalTo(driverId.intValue()))
                .body(TestConstants.FieldNames.EMAIL_FIELD, equalTo(expectedNewEmail));
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void updateDriver_DuplicateDataButForTheSameDriver_ShouldReturnUpdatedDriver() {
        Long driverId = TestConstants.DRIVER_ID;
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();

        Mockito.when(ratingServiceClient.findAverageRating(anyLong(), anyString()))
                .thenReturn(AverageRatingResponse.empty(driverId));

        RestAssuredUtils.updateDriverResponse(driverId, driverRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.ID_FIELD, equalTo(driverId.intValue()));
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void updateDriver_DuplicateEmail_ShouldReturnApiExceptionInfo() {
        Long driverId = 5L;
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        String expectedExceptionMessage = String.format(MessageTemplates.EMAIL_NOT_UNIQUE.getValue(), driverRequest.getEmail());
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response updateDriverResponse = RestAssuredUtils.updateDriverResponse(driverId, driverRequest);

        extractApiExceptionInfoAndAssert(updateDriverResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void updateDriver_DriverIsNotOffline_ShouldReturnApiExceptionInfo() {
        Long driverId = 2L;
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        String expectedExceptionMessage = String.format(MessageTemplates.DRIVER_MODIFYING_NOT_ALLOWED.getValue(), driverId);
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response updateDriverResponse = RestAssuredUtils.updateDriverResponse(driverId, driverRequest);

        extractApiExceptionInfoAndAssert(updateDriverResponse, expectedHttpStatus, expectedExceptionMessage);
    }


    @Test
    void updateDriver_DriverDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long driverId = TestConstants.DRIVER_ID;
        DriverRequest driverRequest = TestUtils.defaultDriverRequest();
        String expectedExceptionMessage = String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_ID.getValue(), driverId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response updateDriverResponse = RestAssuredUtils.updateDriverResponse(driverId, driverRequest);

        extractApiExceptionInfoAndAssert(updateDriverResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void deleteDriver_DriverExists_ShouldDeleteDriver() {
        Long driverId = TestConstants.DRIVER_ID;

        RestAssuredUtils.deleteDriverResponse(driverId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Optional<Driver> deletedDriver = driverRepository.findById(driverId);

        assertTrue(deletedDriver.isEmpty());
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void deleteDriver_DriverIsNotOffline_ShouldReturnApiExceptionInfo() {
        Long driverId = 3L;
        String expectedExceptionMessage = String.format(MessageTemplates.DRIVER_MODIFYING_NOT_ALLOWED.getValue(), driverId);
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Response deleteDriverResponse = RestAssuredUtils.deleteDriverResponse(driverId);

        extractApiExceptionInfoAndAssert(deleteDriverResponse, expectedHttpStatus, expectedExceptionMessage);

    }

    @Test
    void deleteDriver_DriverDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long driverId = TestConstants.DRIVER_ID;
        String expectedExceptionMessage = String.format(MessageTemplates.DRIVER_NOT_FOUND_BY_ID.getValue(), driverId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response deleteDriverResponse = RestAssuredUtils.deleteDriverResponse(driverId);

        extractApiExceptionInfoAndAssert(deleteDriverResponse, expectedHttpStatus, expectedExceptionMessage);
    }


    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void getDriversAvailability_Success() {
        DriverAvailabilityResponse expectedAvailabilityResponse =
                new DriverAvailabilityResponse(5L, 1L);

        DriverAvailabilityResponse availabilityResponse = RestAssuredUtils.getDriverAvailabilityResponse()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverAvailabilityResponse.class);

        assertEquals(expectedAvailabilityResponse, availabilityResponse);
    }

    @ParameterizedTest
    @MethodSource("changeDriverStatusArgumentsProvider")
    @Sql("classpath:insert-drivers-data.sql")
    void changeDriverStatus_ValidChangeDriverStatusRequest_ShouldReturnUpdatedDriver(ChangeDriverStatusRequest request) {
        Mockito.when(paymentServiceClient.findAccountById(any()))
                .thenReturn(DriverAccountResponse.builder().build());

        RestAssuredUtils.changeDriverStatusResponse(request)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.DRIVER_STATUS_FIELD, equalTo(request.getDriverStatus().name()))
                .body(TestConstants.FieldNames.ID_FIELD, equalTo(request.getDriverId().intValue()));
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void changeDriverStatus_DriverAlreadyOnline_ShouldReturnApiExceptionInfo() {
        Long driverId = 2L;
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverStatus(DriverStatus.AVAILABLE)
                .driverId(driverId)
                .build();
        String expectedExceptionMessage = String.format(MessageTemplates.DRIVER_ALREADY_ONLINE.getValue(), driverId);
        HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;

        Response changeDriverStatusResponse = RestAssuredUtils.changeDriverStatusResponse(request);

        extractApiExceptionInfoAndAssert(changeDriverStatusResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-drivers-data.sql")
    void changeDriverStatus_DriverIsNotAvailable_ShouldReturnApiExceptionInfo() {
        long driverId = 3L;
        ChangeDriverStatusRequest request = ChangeDriverStatusRequest.builder()
                .driverStatus(DriverStatus.OFFLINE)
                .driverId(driverId)
                .build();
        String expectedExceptionMessage = String.format(MessageTemplates.DRIVER_NOT_AVAILABLE.getValue(), driverId);
        HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;

        Response changeDriverStatusResponse = RestAssuredUtils.changeDriverStatusResponse(request);

        extractApiExceptionInfoAndAssert(changeDriverStatusResponse, expectedHttpStatus, expectedExceptionMessage);
    }
}
