package com.modsen.ride.dto.request;

import com.modsen.ride.constants.ValidationConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmedRideRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long rideId;
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long driverId;
}
