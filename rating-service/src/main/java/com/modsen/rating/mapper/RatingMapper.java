package com.modsen.rating.mapper;

import com.modsen.rating.dto.request.RatingRequest;
import com.modsen.rating.dto.response.AverageRatingResponse;
import com.modsen.rating.dto.response.PagedRatingResponse;
import com.modsen.rating.dto.response.RatingListResponse;
import com.modsen.rating.dto.response.RatingResponse;
import com.modsen.rating.model.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    Rating toRating(RatingRequest request);

    @Mapping(target = "ratingValue", expression = "java(RatingValue.getValue(rating.getRatingValue()))")
    RatingResponse toRatingResponse(Rating rating);

    AverageRatingResponse toAverageRatingResponse(Rating rating, double averageRating);

    List<RatingResponse> toRatingListResponse(List<Rating> ratingList);

    default PagedRatingResponse toPagedRatingResponse(Page<Rating> ratingPage) {
        return PagedRatingResponse.builder()
                .content(this.toRatingListResponse(ratingPage.getContent()))
                .pageNumber(ratingPage.getNumber())
                .totalPages(ratingPage.getTotalPages())
                .size(ratingPage.getSize())
                .hasPrevious(ratingPage.hasPrevious())
                .hasNext(ratingPage.hasNext())
                .build();
    }
}
