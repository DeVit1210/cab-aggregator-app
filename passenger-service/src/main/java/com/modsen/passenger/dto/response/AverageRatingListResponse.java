package com.modsen.passenger.dto.response;

import com.modsen.passenger.enums.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record AverageRatingListResponse(
        List<AverageRatingResponse> averageRatingResponses,
        Role role,
        int quantity
) {
}
