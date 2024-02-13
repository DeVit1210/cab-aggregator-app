package com.modsen.driver.dto.response;

import com.modsen.driver.enums.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record AverageRatingListResponse(
        List<AverageRatingResponse> averageRatingResponses,
        Role role,
        int quantity
) {
}
