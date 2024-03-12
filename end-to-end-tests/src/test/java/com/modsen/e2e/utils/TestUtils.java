package com.modsen.e2e.utils;

import com.modsen.e2e.constants.TestConstants;
import com.modsen.e2e.dto.request.FinishRideRequest;
import com.modsen.e2e.dto.request.RatingRequest;
import com.modsen.e2e.dto.request.RideCostRequest;
import com.modsen.e2e.dto.request.RideRequest;
import com.modsen.e2e.enums.PaymentType;
import com.modsen.e2e.enums.RatingValue;
import com.modsen.e2e.enums.Role;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {
    public static RideRequest rideRequestForPassenger(Long passengerId) {
        return RideRequest.builder()
                .passengerId(passengerId)
                .destinationAddress(TestConstants.DESTINATION_ADDRESS)
                .pickUpAddress(TestConstants.PICK_UP_ADDRESS)
                .rideCost(TestConstants.RIDE_COST)
                .build();
    }

    public static RideCostRequest defaultCalculateRideCostRequest() {
        return RideCostRequest.builder()
                .passengerId(TestConstants.PASSENGER_ID)
                .pickUpAddress(TestConstants.PICK_UP_ADDRESS)
                .destinationAddress(TestConstants.DESTINATION_ADDRESS)
                .build();
    }

    public static RideRequest defaultRideRequest() {
        return RideRequest.builder()
                .passengerId(TestConstants.PASSENGER_ID)
                .destinationAddress(TestConstants.DESTINATION_ADDRESS)
                .pickUpAddress(TestConstants.PICK_UP_ADDRESS)
                .rideCost(TestConstants.RIDE_COST)
                .build();
    }

    public static FinishRideRequest defaultFinishRideRequest() {
        return FinishRideRequest.builder()
                .id(TestConstants.PASSENGER_ID)
                .paymentType(PaymentType.BY_CARD.name())
                .build();
    }

    public static RatingRequest ratingRequestForDriverAndRide(Long driverId, Long rideId) {
        return RatingRequest.builder()
                .ratedPersonId(driverId)
                .rideId(rideId)
                .role(Role.DRIVER.name())
                .ratingValue(RatingValue.FIVE.name())
                .build();
    }
}
