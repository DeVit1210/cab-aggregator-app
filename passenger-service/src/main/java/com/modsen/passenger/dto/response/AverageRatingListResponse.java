package com.modsen.passenger.dto.response;

import com.modsen.passenger.enums.Role;
import lombok.Builder;

import java.util.Collections;
import java.util.List;

@Builder
public record AverageRatingListResponse(
        List<AverageRatingResponse> averageRatingResponses,
        Role role,
        int quantity
) {
    public static AverageRatingListResponse empty() {
        return AverageRatingListResponse.builder()
                .averageRatingResponses(Collections.emptyList())
                .role(Role.PASSENGER)
                .quantity(0)
                .build();
    }
}
