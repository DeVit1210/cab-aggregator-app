package com.modsen.ride.dto.request;

import com.modsen.ride.constants.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RideCostRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long passengerId;
    @NotBlank(message = ValidationConstants.ADDRESS_NOT_BLANK)
    private String pickUpAddress;
    @NotBlank(message = ValidationConstants.ADDRESS_NOT_BLANK)
    private String destinationAddress;
}
