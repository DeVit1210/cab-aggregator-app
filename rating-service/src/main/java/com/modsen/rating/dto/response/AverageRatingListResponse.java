package com.modsen.rating.dto.response;

import com.modsen.rating.enums.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AverageRatingListResponse {
    private List<AverageRatingResponse> averageRatingResponses;
    private Role role;
    private int quantity;

    public static AverageRatingListResponse of(List<AverageRatingResponse> averageRatingResponses, Role role) {
        return new AverageRatingListResponse(averageRatingResponses, role, averageRatingResponses.size());
    }
}


