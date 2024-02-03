package com.modsen.payment.dto.request;

import com.modsen.payment.constants.ValidationConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DriverPayoutRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long driverId;
    @Positive(message = ValidationConstants.AMOUNT_INVALID)
    private BigDecimal amount;
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long creditCardId;
}
