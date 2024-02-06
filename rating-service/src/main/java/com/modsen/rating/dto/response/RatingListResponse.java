package com.modsen.rating.dto.response;

import java.util.List;

public record RatingListResponse(
        List<RatingResponse> ratingList,
        double averageRating
) {
}
