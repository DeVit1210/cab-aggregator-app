package com.modsen.driver.dto.response;

import com.modsen.driver.enums.Role;
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
                .quantity(0)
                .role(Role.DRIVER)
                .build();
    }
}
