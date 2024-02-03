package com.modsen.payment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DriverAccountResponse {
    public Long driverId;
    private BigDecimal amount;
}
