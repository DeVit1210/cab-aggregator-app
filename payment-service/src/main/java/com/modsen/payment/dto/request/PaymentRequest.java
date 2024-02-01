package com.modsen.payment.dto.request;

import com.modsen.payment.constants.ValidationConstants;
import com.modsen.payment.enums.PaymentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequest {
    @NotEmpty(message = ValidationConstants.ID_NOT_EMPTY)
    private Long passengerId;
    @NotEmpty(message = ValidationConstants.AMOUNT_NOT_EMPTY)
    @Min(value = 0, message = ValidationConstants.AMOUNT_INVALID)
    private BigDecimal amount;
    private PaymentType paymentType;
    @NotEmpty(message = ValidationConstants.ID_NOT_EMPTY)
    private Long rideId;
}