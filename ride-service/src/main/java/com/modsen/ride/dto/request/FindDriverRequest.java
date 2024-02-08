package com.modsen.ride.dto.request;

import com.modsen.ride.constants.ValidationConstants;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindDriverRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long rideId;
}
