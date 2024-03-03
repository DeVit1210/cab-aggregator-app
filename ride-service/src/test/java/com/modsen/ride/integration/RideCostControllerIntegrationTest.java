package com.modsen.ride.integration;

import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.RideCostRequest;
import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import com.modsen.ride.dto.response.DriverAvailabilityResponse;
import com.modsen.ride.dto.response.RideCostResponse;
import com.modsen.ride.enums.RideDemand;
import com.modsen.ride.service.feign.DriverServiceClient;
import com.modsen.ride.service.feign.PromocodeServiceClient;
import com.modsen.ride.utils.RestAssuredUtils;
import com.modsen.ride.utils.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RideCostControllerIntegrationTest extends BaseTestContainer {
    private final BigDecimal startCost = BigDecimal.valueOf(3.00);
    private final BigDecimal kilometerCost = BigDecimal.valueOf(1.50);
    @MockBean
    private DriverServiceClient driverServiceClient;
    @MockBean
    private PromocodeServiceClient promocodeServiceClient;

    static Stream<Arguments> calculateCostArgumentProvider() {
        return Stream.of(
                Arguments.of(new DriverAvailabilityResponse(10, 0), RideDemand.HIGH),
                Arguments.of(new DriverAvailabilityResponse(10, 5), RideDemand.MEDIUM),
                Arguments.of(new DriverAvailabilityResponse(10, 7), RideDemand.LOW)
        );
    }

    @ParameterizedTest
    @MethodSource("calculateCostArgumentProvider")
    void calculateRideCost_ValidRideCostRequestAndPromoApplied_ShouldReturnRideCost(
            DriverAvailabilityResponse driverAvailability,
            RideDemand rideDemand
    ) {
        RideCostRequest rideCostRequest = TestUtils.defaultRideCostRequest();
        AppliedPromocodeResponse appliedPromocodeResponse = TestUtils.defaultAppliedPromocodeResponse();

        when(driverServiceClient.getDriverAvailability())
                .thenReturn(driverAvailability);
        when(promocodeServiceClient.findNotConfirmedPromocode(anyLong()))
                .thenReturn(appliedPromocodeResponse);

        RideCostResponse rideCostResponse = extractRideCostResponse(rideCostRequest);

        double distanceInKm = rideCostResponse.distanceInKm();
        BigDecimal expectedBasicCost = getExpectedBasicCost(distanceInKm, rideDemand);

        assertEquals(rideCostRequest.getPassengerId(), rideCostResponse.passengerId());
        assertEquals(rideDemand, rideCostResponse.rideDemand());
        assertEquals(expectedBasicCost, rideCostResponse.rideCost());
        assertEquals(getDiscountedCost(expectedBasicCost), rideCostResponse.discountedCost());
    }

    @ParameterizedTest
    @MethodSource("calculateCostArgumentProvider")
    void calculateRideCost_ValidRideCostRequestAndPromoNotApplied_ShouldReturnRideCost(
            DriverAvailabilityResponse driverAvailability,
            RideDemand rideDemand
    ) {
        RideCostRequest rideCostRequest = TestUtils.defaultRideCostRequest();

        when(driverServiceClient.getDriverAvailability())
                .thenReturn(driverAvailability);
        when(promocodeServiceClient.findNotConfirmedPromocode(anyLong()))
                .thenReturn(AppliedPromocodeResponse.empty());

        RideCostResponse rideCostResponse = extractRideCostResponse(rideCostRequest);

        double distanceInKm = rideCostResponse.distanceInKm();
        BigDecimal expectedBasicCost = getExpectedBasicCost(distanceInKm, rideDemand);

        assertEquals(rideCostRequest.getPassengerId(), rideCostResponse.passengerId());
        assertEquals(rideDemand, rideCostResponse.rideDemand());
        assertEquals(expectedBasicCost, rideCostResponse.rideCost());
        assertEquals(expectedBasicCost, rideCostResponse.discountedCost());
    }


    private BigDecimal getExpectedBasicCost(double rideDistance, RideDemand rideDemand) {
        return BigDecimal.valueOf(rideDistance).multiply(kilometerCost)
                .add(startCost)
                .multiply(BigDecimal.valueOf(rideDemand.getRideCostCoefficient()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getDiscountedCost(BigDecimal basicRideCost) {
        return basicRideCost
                .subtract(
                        basicRideCost.multiply(BigDecimal.valueOf(TestConstants.PROMOCODE_DISCOUNT_PERCENT_DOUBLE))
                )
                .setScale(2, RoundingMode.HALF_UP);
    }

    private RideCostResponse extractRideCostResponse(RideCostRequest rideCostRequest) {
        return RestAssuredUtils.calculateRideCostResponse(rideCostRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideCostResponse.class);
    }
}
