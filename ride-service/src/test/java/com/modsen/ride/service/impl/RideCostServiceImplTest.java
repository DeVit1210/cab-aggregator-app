package com.modsen.ride.service.impl;

import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.RideCostRequest;
import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import com.modsen.ride.dto.response.DriverAvailabilityResponse;
import com.modsen.ride.dto.response.RideCostResponse;
import com.modsen.ride.enums.RideDemand;
import com.modsen.ride.service.DistanceCalculator;
import com.modsen.ride.service.feign.DriverServiceClient;
import com.modsen.ride.service.feign.PromocodeServiceClient;
import com.modsen.ride.utils.TestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideCostServiceImplTest {
    @Mock
    private DriverServiceClient driverServiceClient;
    @Mock
    private PromocodeServiceClient promocodeServiceClient;
    @Mock
    private DistanceCalculator distanceCalculator;
    @InjectMocks
    private RideCostServiceImpl rideCostService;
    @Value("${ride.cost.start}")
    private BigDecimal startCost = BigDecimal.valueOf(3.00);
    @Value("${ride.cost.per-kilometer}")
    private BigDecimal kilometerCost = BigDecimal.valueOf(1.50);

    static Stream<Arguments> calculateCostArgumentProvider() {
        return Stream.of(
                Arguments.of(
                        new DriverAvailabilityResponse(10, 0),
                        TestConstants.RIDE_DISTANCE,
                        RideDemand.HIGH
                ),
                Arguments.of(
                        new DriverAvailabilityResponse(10, 5),
                        TestConstants.RIDE_DISTANCE,
                        RideDemand.MEDIUM
                ),
                Arguments.of(
                        new DriverAvailabilityResponse(10, 7),
                        TestConstants.RIDE_DISTANCE,
                        RideDemand.LOW
                )
        );
    }

    @ParameterizedTest
    @MethodSource("calculateCostArgumentProvider")
    void calculateCost_ValidRideCostRequestAndPromoApplied_ReturnCalculatedCost(
            DriverAvailabilityResponse driverAvailability,
            double rideDistance,
            RideDemand rideDemand) {

        RideCostRequest rideCostRequest = TestUtils.defaultRideCostRequest();
        AppliedPromocodeResponse appliedPromocodeResponse = TestUtils.defaultAppliedPromocodeResponse();
        BigDecimal expectedBasicCost = BigDecimal.valueOf(rideDistance).multiply(kilometerCost)
                .add(startCost)
                .multiply(BigDecimal.valueOf(rideDemand.getRideCostCoefficient()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedDiscountedCost = expectedBasicCost.subtract(
                expectedBasicCost.multiply(BigDecimal.valueOf(TestConstants.PROMOCODE_DISCOUNT_PERCENT_DOUBLE))
        ).setScale(2, RoundingMode.HALF_UP);

        when(driverServiceClient.getDriverAvailability())
                .thenReturn(driverAvailability);
        when(distanceCalculator.calculateDistance(any(RideCostRequest.class)))
                .thenReturn(rideDistance);
        when(promocodeServiceClient.findNotConfirmedPromocode(anyLong()))
                .thenReturn(appliedPromocodeResponse);

        RideCostResponse rideCostResponse = rideCostService.calculateCost(rideCostRequest);

        assertEquals(TestConstants.RIDE_DISTANCE, rideCostResponse.distanceInKm());
        assertEquals(rideDistance, rideCostResponse.distanceInKm());
        assertEquals(rideDemand, rideCostResponse.rideDemand());
        assertEquals(expectedBasicCost, rideCostResponse.rideCost());
        assertEquals(expectedDiscountedCost, rideCostResponse.discountedCost());
    }
}