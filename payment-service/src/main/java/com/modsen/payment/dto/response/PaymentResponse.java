package com.modsen.payment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long passengerId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
