package com.modsen.rating.service;

import com.modsen.rating.dto.request.PageSettingRequest;
import com.modsen.rating.dto.request.RatingRequest;
import com.modsen.rating.dto.response.AverageRatingResponse;
import com.modsen.rating.dto.response.PagedRatingResponse;
import com.modsen.rating.dto.response.RatingListResponse;
import com.modsen.rating.dto.response.RatingResponse;
import com.modsen.rating.enums.RatingValue;
import com.modsen.rating.enums.Role;

public interface RatingService {
    RatingResponse createRating(RatingRequest request);

    RatingListResponse getAllRatings(Long ratedPersonId, Role role);

    AverageRatingResponse getAverageRating(Long ratedPersonId, Role role);

    PagedRatingResponse getRatings(Long ratedPersonId, Role role, PageSettingRequest request);

    RatingResponse getRatingById(Long ratingId);

    RatingResponse updateRating(Long ratingId, RatingValue ratingValue);
}
