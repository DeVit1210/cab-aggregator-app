package com.modsen.rating.dto.response;

import com.modsen.rating.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponse {
    private Long ratedPersonId;
    private Role role;
    private int ratingValue;
    private LocalDateTime createdAt;
    private Long rideId;
}
