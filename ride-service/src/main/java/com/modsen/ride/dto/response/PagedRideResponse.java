package com.modsen.ride.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PagedRideResponse(
        List<RideResponse> content,
        int pageNumber,
        int totalPages,
        int size,
        boolean hasPrevious,
        boolean hasNext
) {
}
