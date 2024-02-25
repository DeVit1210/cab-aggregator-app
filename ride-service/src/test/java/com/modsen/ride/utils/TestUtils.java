package com.modsen.ride.utils;

import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.ChangeDriverStatusRequest;
import com.modsen.ride.dto.request.FindDriverRequest;
import com.modsen.ride.dto.request.FinishRideRequest;
import com.modsen.ride.dto.request.PaymentRequest;
import com.modsen.ride.dto.request.RideCostRequest;
import com.modsen.ride.dto.request.RideRequest;
import com.modsen.ride.dto.request.UpdateRideDriverRequest;
import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import com.modsen.ride.dto.response.RideListResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.dto.response.ShortRideResponse;
import com.modsen.ride.enums.DriverStatus;
import com.modsen.ride.enums.PaymentType;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.model.Ride;
import lombok.experimental.UtilityClass;

import java.util.Collections;

@UtilityClass
public class TestUtils {
    public static Ride defaultRide() {
        return Ride.builder()
                .id(TestConstants.RIDE_ID)
                .driverId(TestConstants.DRIVER_ID)
                .passengerId(TestConstants.PASSENGER_ID)
                .rideCost(TestConstants.RIDE_COST)
                .rideStatus(RideStatus.FINISHED)
                .build();
    }

    public static Ride rideWithStatus(RideStatus rideStatus) {
        return Ride.builder()
                .id(TestConstants.RIDE_ID)
                .driverId(TestConstants.DRIVER_ID)
                .passengerId(TestConstants.PASSENGER_ID)
                .rideCost(TestConstants.RIDE_COST)
                .rideStatus(rideStatus)
                .build();
    }

    public static RideResponse defaultRideResponse() {
        return RideResponse.builder()
                .id(TestConstants.RIDE_ID)
                .driverId(TestConstants.DRIVER_ID)
                .passengerId(TestConstants.PASSENGER_ID)
                .rideCost(TestConstants.RIDE_COST)
                .rideStatus(RideStatus.FINISHED)
                .build();
    }

    public static ShortRideResponse defaultShortRideResponse() {
        return ShortRideResponse.builder()
                .id(TestConstants.RIDE_ID)
                .driverId(TestConstants.DRIVER_ID)
                .passengerId(TestConstants.PASSENGER_ID)
                .rideCost(TestConstants.RIDE_COST)
                .rideStatus(RideStatus.FINISHED)
                .build();
    }

    public static RideListResponse defaultRideListResponse() {
        return RideListResponse.of(Collections.emptyList());
    }

    public static RideRequest defaultRideRequest() {
        return RideRequest.builder()
                .passengerId(TestConstants.PASSENGER_ID)
                .rideCost(TestConstants.RIDE_COST)
                .build();
    }

    public static FindDriverRequest defaultFindDriverRequest() {
        return new FindDriverRequest(TestConstants.RIDE_ID);
    }

    public static UpdateRideDriverRequest updateRideDriverRequestWithDriver() {
        return UpdateRideDriverRequest.builder()
                .rideId(TestConstants.RIDE_ID)
                .isDriverAvailable(true)
                .driverId(TestConstants.DRIVER_ID)
                .build();
    }

    public static UpdateRideDriverRequest updateRideDriverRequestWithoutDriver() {
        return UpdateRideDriverRequest.builder()
                .rideId(TestConstants.RIDE_ID)
                .isDriverAvailable(false)
                .build();
    }

    public static ChangeDriverStatusRequest changeDriverStatusRequestWithStatus(DriverStatus driverStatus) {
        return ChangeDriverStatusRequest.builder()
                .driverId(TestConstants.DRIVER_ID)
                .driverStatus(driverStatus)
                .build();
    }

    public static FinishRideRequest defaultFinishRideRequest() {
        return FinishRideRequest.builder()
                .id(TestConstants.RIDE_ID)
                .paymentType(PaymentType.BY_CARD.name())
                .build();
    }

    public static PaymentRequest defaultPaymentRequest() {
        return PaymentRequest.builder()
                .rideId(TestConstants.RIDE_ID)
                .driverId(TestConstants.DRIVER_ID)
                .passengerId(TestConstants.PASSENGER_ID)
                .amount(TestConstants.RIDE_COST)
                .type(PaymentType.BY_CARD.name())
                .build();
    }

    public static AppliedPromocodeResponse defaultAppliedPromocodeResponse() {
        return AppliedPromocodeResponse.builder()
                .id(TestConstants.PROMOCODE_ID)
                .discountPercent(TestConstants.PROMOCODE_DISCOUNT_PERCENT)
                .passengerId(TestConstants.PASSENGER_ID)
                .build();
    }

    public static RideCostRequest defaultRideCostRequest() {
        return RideCostRequest.builder()
                .passengerId(TestConstants.PASSENGER_ID)
                .build();
    }
}
