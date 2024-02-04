package com.modsen.driver.dto.request;

import com.modsen.driver.constants.ValidationConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RideRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long rideId;
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long passengerId;
    @NotBlank(message = ValidationConstants.ADDRESS_NOT_BLANK)
    private String pickUpAddress;
    @NotBlank(message = ValidationConstants.ADDRESS_NOT_BLANK)
    private String destinationAddress;
    @NotNull
    @Min(value = 0, message = ValidationConstants.NEGATIVE_AMOUNT)
    private BigDecimal cost;
}
