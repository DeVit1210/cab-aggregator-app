package com.modsen.rating.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RatingListResponse {
    private final List<RatingResponse> ratingList;
    private double averageRating;

    public static RatingListResponse of(List<RatingResponse> ratingList) {
        Double averageRating = ratingList.stream()
                .collect(Collectors.averagingDouble(RatingResponse::getRatingValue));

        return new RatingListResponse(ratingList, averageRating);
    }
}
