package com.modsen.ride.dto.request;

import com.modsen.ride.constants.ValidationConstants;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRideDriverRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long rideId;
    @NotNull
    private boolean isDriverAvailable;
    private Long driverId;
}
