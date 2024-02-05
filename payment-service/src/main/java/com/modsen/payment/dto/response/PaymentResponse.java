package com.modsen.payment.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponse(
        Long id,
        Long passengerId,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
