package com.modsen.driver.dto.request;

import com.modsen.driver.constants.ValidationConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RideRequest {
    private Long rideId;
    @NotEmpty(message = ValidationConstants.ID_NOT_EMPTY)
    private Long passengerId;
    @NotEmpty(message = ValidationConstants.ADDRESS_NOT_EMPTY)
    private String pickUpAddress;
    @NotEmpty(message = ValidationConstants.ADDRESS_NOT_EMPTY)
    private String destinationAddress;
    @NotEmpty
    @Min(value = 0, message = ValidationConstants.NEGATIVE_AMOUNT)
    private BigDecimal cost;
}
