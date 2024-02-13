package com.modsen.driver.dto.request;

import com.modsen.driver.constants.ValidationConstants;
import com.modsen.driver.enums.DriverStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeDriverStatusRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private long driverId;
    private DriverStatus driverStatus;
}
