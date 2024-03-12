package com.modsen.e2e.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingRequest {
    private Long ratedPersonId;
    private String role;
    private String ratingValue;
    private String comment;
    private Long rideId;
}
