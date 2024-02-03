package com.modsen.payment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DriverPayoutResponse {
    private Long id;
    private Long driverId;
    private BigDecimal withdrawAmount;
    private BigDecimal leftoverAmount;
    private Long creditCardId;
}
