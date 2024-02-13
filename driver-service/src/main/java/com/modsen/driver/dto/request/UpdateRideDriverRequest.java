package com.modsen.driver.dto.request;

import com.modsen.driver.constants.ValidationConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRideDriverRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long rideId;
    @NotNull
    private boolean isDriverAvailable;
    private Long driverId;
}
