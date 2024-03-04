package com.modsen.promocode.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PromocodeResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        int discountPercent,
        int minRidesQuantity,
        int maxUsageCount
) {
}
