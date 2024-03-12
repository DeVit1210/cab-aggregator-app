package com.modsen.e2e.dto.response;


import java.util.List;

public record RatingListResponse(
        List<RatingResponse> ratingList,
        double averageRating
) {
}
