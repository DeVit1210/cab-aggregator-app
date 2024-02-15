package com.modsen.passenger.dto.response;

import com.modsen.passenger.enums.Role;
import lombok.Builder;

@Builder
public record AverageRatingResponse(
        Long ratedPersonId,
        Role role,
        double averageRating,
        int ratesTotalQuantity
) {
    public static AverageRatingResponse empty(Long ratedPersonId) {
        return new AverageRatingResponse(ratedPersonId, Role.PASSENGER, 5.0, 0);
    }
}
