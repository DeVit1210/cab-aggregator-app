package com.modsen.promocode.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PromocodeResponse {
    private Long id;
    private LocalDate startTime;
    private LocalDate endTime;
    private int discountPercent;
    private int minRidesQuantity;
    private int maxUsageCount;
}
