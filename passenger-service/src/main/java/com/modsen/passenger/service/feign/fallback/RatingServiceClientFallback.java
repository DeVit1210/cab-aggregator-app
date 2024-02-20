package com.modsen.passenger.service.feign.fallback;

import com.modsen.passenger.dto.response.AverageRatingListResponse;
import com.modsen.passenger.dto.response.AverageRatingResponse;
import com.modsen.passenger.service.feign.RatingServiceClient;

public class RatingServiceClientFallback implements RatingServiceClient {
    @Override
    public AverageRatingListResponse findAllAverageRatings(String role) {
        return AverageRatingListResponse.empty();
    }

    @Override
    public AverageRatingResponse findAverageRating(Long ratedPersonId, String role) {
        return AverageRatingResponse.empty(ratedPersonId);
    }
}
