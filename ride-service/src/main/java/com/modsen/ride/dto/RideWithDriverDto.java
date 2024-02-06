package com.modsen.ride.dto;

import com.modsen.ride.constants.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RideWithDriverDto {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long passengerId;
    @NotNull
    @Positive(message = ValidationConstants.RIDE_COST_INVALID)
    private BigDecimal rideCost;
    @NotBlank(message = ValidationConstants.ADDRESS_NOT_BLANK)
    private String pickUpAddress;
    @NotBlank(message = ValidationConstants.ADDRESS_NOT_BLANK)
    private String destinationAddress;
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long driverId;
}
