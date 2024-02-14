package com.modsen.passenger.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PagedPassengerResponse(
        List<ShortPassengerResponse> content,
        int pageNumber,
        int pageSize,
        int totalPages,
        boolean hasPrevious,
        boolean hasNext
) {
}
