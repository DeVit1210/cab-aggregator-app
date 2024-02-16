package com.modsen.rating.dto.response;

import com.modsen.rating.enums.Role;
import lombok.Builder;

@Builder
public record AverageRatingResponse(
        Long ratedPersonId,
        Role role,
        double averageRating,
        int ratesQuantity
) {
}
