package com.modsen.ride.dto.request;

import com.modsen.ride.constants.ValidationConstants;
import com.modsen.ride.enums.PaymentType;
import com.modsen.ride.validation.EnumValue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinishRideRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long id;
    @EnumValue(enumClass = PaymentType.class)
    private String paymentType;
}
