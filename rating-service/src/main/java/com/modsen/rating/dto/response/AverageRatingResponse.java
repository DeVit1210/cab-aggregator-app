package com.modsen.rating.dto.response;

import com.modsen.rating.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AverageRatingResponse {
    private Long ratedPersonId;
    private Role role;
    private double averageRating;
}
