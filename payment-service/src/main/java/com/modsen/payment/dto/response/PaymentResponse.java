package com.modsen.payment.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponse(
        Long id,
        Long passengerId,
        Long rideId,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
