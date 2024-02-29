package com.modsen.rating.intergration;

import com.modsen.rating.constants.MessageTemplates;
import com.modsen.rating.constants.ServiceMappings;
import com.modsen.rating.constants.TestConstants;
import com.modsen.rating.dto.request.RatingRequest;
import com.modsen.rating.dto.response.AverageRatingResponse;
import com.modsen.rating.dto.response.RatingListResponse;
import com.modsen.rating.dto.response.RatingResponse;
import com.modsen.rating.dto.response.RideResponse;
import com.modsen.rating.enums.RatingValue;
import com.modsen.rating.enums.Role;
import com.modsen.rating.exception.ApiExceptionInfo;
import com.modsen.rating.exception.base.NotFoundException;
import com.modsen.rating.model.Rating;
import com.modsen.rating.repository.RatingRepository;
import com.modsen.rating.service.feign.RideServiceClient;
import com.modsen.rating.utils.RestAssuredUtils;
import com.modsen.rating.utils.TestUtils;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RatingServiceIntegrationTests {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.2.0"));
    @LocalServerPort
    private Integer port;
    @Autowired
    private RatingRepository ratingRepository;
    @MockBean
    private RideServiceClient rideServiceClient;

    static Stream<Arguments> findAllRatingsForPersonArgumentsProvider() {
        return Stream.of(
                Arguments.of(1L, Role.DRIVER.name(), 3, BigDecimal.valueOf(3.33)),
                Arguments.of(1L, Role.PASSENGER.name(), 1, BigDecimal.valueOf(5.0)),
                Arguments.of(2L, Role.DRIVER.name(), 0, BigDecimal.valueOf(5.0))
        );
    }

    static Stream<Arguments> findAverageRatingArgumentsProvider() {
        return Stream.of(
                Arguments.of(1L, Role.DRIVER.name(), BigDecimal.valueOf(3.33)),
                Arguments.of(1L, Role.PASSENGER.name(), BigDecimal.valueOf(5.00)),
                Arguments.of(2L, Role.DRIVER.name(), BigDecimal.valueOf(5.00))
        );
    }

    static Stream<Arguments> findAllAverageRatingsArgumentsProvider() {
        return Stream.of(
                Arguments.of(Role.DRIVER.name(), 1),
                Arguments.of(Role.PASSENGER.name(), 1)
        );
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = ServiceMappings.RATING_CONTROLLER;
    }

    @AfterEach
    void tearDown() {
        ratingRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("findAllRatingsForPersonArgumentsProvider")
    @Sql("classpath:insert-ratings-data.sql")
    void findAllRatingsForPerson_Success(Long ratedPersonId, String role, int expectedSize, BigDecimal expectedRating) {
        RatingListResponse ratingListResponse = RestAssuredUtils.getAllRatingsForPersonResponse(ratedPersonId, role)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RatingListResponse.class);
        BigDecimal averageRating = BigDecimal.valueOf(ratingListResponse.averageRating())
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedSize, ratingListResponse.ratingList().size());
        assertEquals(expectedRating.setScale(2, RoundingMode.HALF_UP), averageRating);
    }

    @ParameterizedTest
    @MethodSource("findAverageRatingArgumentsProvider")
    @Sql("classpath:insert-ratings-data.sql")
    void findAverageRating_Success(Long ratedPersonId, String role, BigDecimal expectedRating) {
        AverageRatingResponse averageRating = RestAssuredUtils.getAverageRatingResponse(ratedPersonId, role)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AverageRatingResponse.class);

        BigDecimal ratingValue = BigDecimal.valueOf(averageRating.averageRating())
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedRating.setScale(2, RoundingMode.HALF_UP), ratingValue);
    }

    @ParameterizedTest
    @MethodSource("findAllAverageRatingsArgumentsProvider")
    @Sql("classpath:insert-ratings-data.sql")
    void findAllAverageRatings_Success(String role, int expectedRatingsQuantity) {
        RestAssuredUtils.getAllAverageRatingsResponse(role)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.ROLE_FIELD, equalTo(role))
                .body(TestConstants.FieldNames.QUANTITY_FIELD, equalTo(expectedRatingsQuantity));
    }

    @Test
    @Sql("classpath:insert-ratings-data.sql")
    void getRatingById_RatingExists_ShouldReturnRating() {
        Rating expectedRating = Rating.builder()
                .id(TestConstants.RATING_ID)
                .ratedPersonId(1L)
                .role(Role.DRIVER)
                .ratingValue(RatingValue.FIVE)
                .build();
        Long ratingId = TestConstants.RATING_ID;

        RestAssuredUtils.getRatingResponse(ratingId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.RATED_PERSON_ID_FIELD, equalTo(expectedRating.getRatedPersonId().intValue()))
                .body(TestConstants.FieldNames.RATING_VALUE_FIELD, equalTo(expectedRating.getRatingValue().ordinal()))
                .body(TestConstants.FieldNames.ROLE_FIELD, equalTo(expectedRating.getRole().name()));
    }

    @Test
    void getRatingById_RatingDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long ratingId = TestConstants.RATING_ID;
        String expectedExceptionMessage = String.format(MessageTemplates.RATING_NOT_FOUND.getValue(), ratingId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        ApiExceptionInfo apiExceptionInfo = RestAssuredUtils.getRatingResponse(ratingId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedExceptionMessage, apiExceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, apiExceptionInfo.getHttpStatus());
    }

    @Test
    void createRating_ValidRatingRequest_ShouldReturnCreatedRating() {
        RatingRequest request = TestUtils.ratingRequestForRole(Role.PASSENGER);
        Rating expectedRating = TestUtils.defaultRating(Role.PASSENGER);
        RideResponse rideResponse = TestUtils.defaultRideResponse();

        Mockito.when(rideServiceClient.findRideById(anyLong()))
                .thenReturn(rideResponse);

        RatingResponse ratingResponse = RestAssuredUtils.createRatingResponse(request)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(RatingResponse.class);

        Optional<Rating> createdRating = ratingRepository.findById(ratingResponse.id());

        assertTrue(createdRating.isPresent());
        assertThat(createdRating.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "comment", "createdAt")
                .isEqualTo(expectedRating);
    }

    @Test
    void createRating_RideDoesNotExist_ShouldReturnApiExceptionInfo() {
        RatingRequest request = TestUtils.ratingRequestForRole(Role.PASSENGER);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Mockito.when(rideServiceClient.findRideById(anyLong()))
                .thenThrow(NotFoundException.class);

        ApiExceptionInfo apiExceptionInfo = RestAssuredUtils.createRatingResponse(request)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedHttpStatus, apiExceptionInfo.getHttpStatus());
    }

    @Test
    void createRating_InvalidRatedPersonForRide_ShouldReturnApiExceptionInfo() {
        Long nonExistentPassengerId = 2L;
        RatingRequest request = TestUtils.ratingRequestForRole(Role.PASSENGER);
        RideResponse rideResponse = TestUtils.rideResponseWithPassengerId(nonExistentPassengerId);
        String expectedExceptionMessage = String.format(
                MessageTemplates.ILLEGAL_ID_FOR_RIDE.getValue(),
                Role.PASSENGER.name(),
                nonExistentPassengerId
        );
        HttpStatus expectedHttpStatus = HttpStatus.BAD_REQUEST;

        Mockito.when(rideServiceClient.findRideById(anyLong()))
                .thenReturn(rideResponse);

        ApiExceptionInfo apiExceptionInfo = RestAssuredUtils.createRatingResponse(request)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedExceptionMessage, apiExceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, apiExceptionInfo.getHttpStatus());
    }

    @Test
    @Sql("classpath:insert-ratings-data.sql")
    void createRating_RatingAlreadyExists_ShouldReturnApiExceptionInfo() {
        RatingRequest request = TestUtils.ratingRequestForRole(Role.PASSENGER);
        RideResponse rideResponse = TestUtils.defaultRideResponse();
        String expectedExceptionMessage = String.format(
                MessageTemplates.RATING_ALREADY_EXISTS.getValue(),
                Role.PASSENGER.name(),
                request.getRideId()
        );
        HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;

        Mockito.when(rideServiceClient.findRideById(anyLong()))
                .thenReturn(rideResponse);

        ApiExceptionInfo apiExceptionInfo = RestAssuredUtils.createRatingResponse(request)
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedExceptionMessage, apiExceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, apiExceptionInfo.getHttpStatus());
    }

    @Test
    @Sql("classpath:insert-ratings-data.sql")
    void updateRating_ValidRatingIdAndRatingValue_ShouldReturnUpdatedRating() {
        Long ratingId = TestConstants.RATING_ID;
        RatingValue expectedNewRatingValue = RatingValue.THREE;

        RestAssuredUtils.updateRatingResponse(ratingId, expectedNewRatingValue.name())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.RATING_VALUE_FIELD, equalTo(expectedNewRatingValue.ordinal()));

        Optional<Rating> rating = ratingRepository.findById(ratingId);

        assertTrue(rating.isPresent());
        assertEquals(expectedNewRatingValue, rating.get().getRatingValue());
    }

    @Test
    void updateRating_RatingDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long ratingId = TestConstants.RATING_ID;
        String expectedExceptionMessage = String.format(MessageTemplates.RATING_NOT_FOUND.getValue(), ratingId);
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        ApiExceptionInfo apiExceptionInfo = RestAssuredUtils.updateRatingResponse(ratingId, RatingValue.ZERO.name())
                .then()
                .assertThat()
                .statusCode(expectedHttpStatus.value())
                .extract()
                .as(ApiExceptionInfo.class);

        assertEquals(expectedExceptionMessage, apiExceptionInfo.getMessage());
        assertEquals(expectedHttpStatus, apiExceptionInfo.getHttpStatus());
    }
}
