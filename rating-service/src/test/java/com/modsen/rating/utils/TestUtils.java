package com.modsen.rating.utils;

import com.modsen.rating.constants.TestConstants;
import com.modsen.rating.dto.request.RatingRequest;
import com.modsen.rating.dto.response.RideResponse;
import com.modsen.rating.enums.RatingValue;
import com.modsen.rating.enums.Role;
import com.modsen.rating.model.Rating;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class TestUtils {
    public static List<Rating> ratingForRoleAndRatingValues(Role role, List<RatingValue> ratingValues) {
        return ratingValues.stream()
                .map(value -> buildForRoleAndRatingValue(role, value))
                .toList();
    }

    public static List<Rating> ratingForRoleAndRatedPeople(Role role, List<Long> ratedPersonIdList) {
        return ratedPersonIdList.stream()
                .map(id -> buildForRoleAndRatedPerson(role, id))
                .toList();
    }

    public static Rating defaultRating(Role role) {
        return Rating.builder()
                .id(TestConstants.RATING_ID)
                .ratedPersonId(TestConstants.RATED_PERSON_ID)
                .role(role)
                .ratingValue(RatingValue.FIVE)
                .rideId(TestConstants.RIDE_ID)
                .build();
    }

    public static RatingRequest ratingRequestForRole(Role role) {
        return RatingRequest.builder()
                .ratedPersonId(TestConstants.RATED_PERSON_ID)
                .ratingValue(RatingValue.FIVE.name())
                .role(role.name())
                .rideId(TestConstants.RIDE_ID)
                .build();
    }

    private static Rating buildForRoleAndRatingValue(Role role, RatingValue ratingValue) {
        return Rating.builder()
                .ratingValue(ratingValue)
                .role(role)
                .build();
    }

    private static Rating buildForRoleAndRatedPerson(Role role, Long id) {
        return Rating.builder()
                .ratedPersonId(id)
                .role(role)
                .ratingValue(RatingValue.FIVE)
                .build();
    }

    public static RideResponse defaultRideResponse() {
        return rideResponseWithPassengerId(TestConstants.PASSENGER_ID);
    }

    public static RideResponse rideResponseWithPassengerId(Long passengerId) {
        return RideResponse.builder()
                .id(TestConstants.RIDE_ID)
                .driverId(TestConstants.DRIVER_ID)
                .passengerId(passengerId)
                .build();
    }
}
