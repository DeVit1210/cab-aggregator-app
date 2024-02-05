package com.modsen.payment.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DriverPayoutResponse(
        Long id,
        Long driverId,
        BigDecimal withdrawAmount,
        BigDecimal leftoverAmount,
        Long creditCardId
) {
}
