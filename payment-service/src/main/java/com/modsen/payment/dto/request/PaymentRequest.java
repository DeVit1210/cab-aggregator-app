package com.modsen.payment.dto.request;

import com.modsen.payment.constants.ValidationConstants;
import com.modsen.payment.enums.PaymentType;
import com.modsen.payment.validation.EnumValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long passengerId;
    @NotNull
    @Positive(message = ValidationConstants.AMOUNT_INVALID)
    private BigDecimal amount;
    @EnumValue(enumClass = PaymentType.class)
    private String type;
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long rideId;
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long driverId;
}