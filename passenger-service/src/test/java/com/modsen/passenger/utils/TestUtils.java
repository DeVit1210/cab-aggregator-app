package com.modsen.passenger.utils;

import com.modsen.passenger.constants.TestConstants;
import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.AverageRatingListResponse;
import com.modsen.passenger.dto.response.AverageRatingResponse;
import com.modsen.passenger.enums.Role;
import com.modsen.passenger.model.Passenger;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class TestUtils {
    public static Passenger defaultPassenger() {
        return Passenger.builder()
                .id(TestConstants.PASSENGER_ID)
                .email(TestConstants.PASSENGER_EMAIL)
                .phoneNumber(TestConstants.PASSENGER_PHONE_NUMBER)
                .firstName(TestConstants.PASSENGER_FIRST_NAME)
                .lastName(TestConstants.PASSENGER_SECOND_NAME)
                .build();
    }

    public static PassengerRequest defaultPassengerRequest() {
        return PassengerRequest.builder()
                .email(TestConstants.PASSENGER_EMAIL)
                .phoneNumber(TestConstants.PASSENGER_PHONE_NUMBER)
                .firstName(TestConstants.PASSENGER_FIRST_NAME)
                .lastName(TestConstants.PASSENGER_SECOND_NAME)
                .build();
    }

    public static PassengerRequest passengerRequestWithPhoneNumber(String phoneNumber) {
        return passengerRequestWithPhoneNumberAndEmail(phoneNumber, TestConstants.PASSENGER_EMAIL);
    }

    public static PassengerRequest passengerRequestWithEmail(String email) {
        return passengerRequestWithPhoneNumberAndEmail(TestConstants.PASSENGER_PHONE_NUMBER, email);
    }

    private static PassengerRequest passengerRequestWithPhoneNumberAndEmail(String phoneNumber, String email) {
        return PassengerRequest.builder()
                .email(email)
                .phoneNumber(phoneNumber)
                .firstName(TestConstants.PASSENGER_FIRST_NAME)
                .lastName(TestConstants.PASSENGER_SECOND_NAME)
                .build();
    }

    public static AverageRatingListResponse averageRatingListResponse(List<AverageRatingResponse> averageRatingResponses,
                                                                      Role role) {
        return new AverageRatingListResponse(averageRatingResponses, role, averageRatingResponses.size());
    }
}
