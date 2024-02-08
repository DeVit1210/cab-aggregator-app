package com.modsen.ride.dto.request;

import com.modsen.ride.constants.ValidationConstants;
import com.modsen.ride.enums.DriverStatus;
import com.modsen.ride.validation.EnumValue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeDriverStatusRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long driverId;
    @EnumValue(enumClass = DriverStatus.class)
    private DriverStatus driverStatus;
}
