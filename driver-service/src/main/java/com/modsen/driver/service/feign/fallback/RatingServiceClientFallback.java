package com.modsen.driver.service.feign.fallback;

import com.modsen.driver.dto.response.AverageRatingListResponse;
import com.modsen.driver.dto.response.AverageRatingResponse;
import com.modsen.driver.service.feign.RatingServiceClient;

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
