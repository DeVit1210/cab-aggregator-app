package com.modsen.driver.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PagedDriverResponse(
        List<DriverResponse> content,
        int pageNumber,
        int pageSize,
        int totalPageCount,
        boolean hasPrevious,
        boolean hasNext
) {
}
