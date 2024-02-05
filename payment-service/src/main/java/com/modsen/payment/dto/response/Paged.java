package com.modsen.payment.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record Paged<T>(
        List<T> content,
        int pageNumber,
        int totalPageQuantity,
        int pageSize,
        boolean hasPrevious,
        boolean hasNext
) {
}
