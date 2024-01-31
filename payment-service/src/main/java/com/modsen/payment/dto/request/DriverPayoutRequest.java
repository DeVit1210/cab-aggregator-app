package com.modsen.payment.dto.request;

import com.modsen.payment.constants.ValidationConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DriverPayoutRequest {
    @NotEmpty
    private Long driverId;
    @NotEmpty(message = ValidationConstants.AMOUNT_NOT_EMPTY)
    @Min(value = 0, message = ValidationConstants.AMOUNT_INVALID)
    private BigDecimal amount;
    @NotEmpty
    private Long creditCardId;
}
