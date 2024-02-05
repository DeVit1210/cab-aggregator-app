package com.modsen.payment.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DriverAccountResponse(
        Long driverId,
        BigDecimal amount
) {
}
