package com.modsen.rating.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RatingListResponse {
    private final List<RatingResponse> ratingList;
    private double averageRating;
}
