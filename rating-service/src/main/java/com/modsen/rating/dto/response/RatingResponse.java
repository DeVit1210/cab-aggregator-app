package com.modsen.rating.dto.response;

import com.modsen.rating.enums.Role;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RatingResponse(
        Long id,
        Long ratedPersonId,
        Role role,
        int ratingValue,
        LocalDateTime createdAt,
        String comment,
        Long rideId
) {
}
