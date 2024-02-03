package com.modsen.rating.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedRatingResponse {
    private List<RatingResponse> content;
    private int pageNumber;
    private int totalPages;
    private int size;
    private boolean hasPrevious;
    private boolean hasNext;
}
