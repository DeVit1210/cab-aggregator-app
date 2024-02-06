package com.modsen.rating.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PagedRatingResponse(
        List<RatingResponse> content,
        int pageNumber,
        int totalPages,
        int size,
        boolean hasPrevious,
        boolean hasNext
) {
}
