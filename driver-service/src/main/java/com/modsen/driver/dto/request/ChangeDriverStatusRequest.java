package com.modsen.driver.dto.request;

import com.modsen.driver.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeDriverStatusRequest {
    private long driverId;
    private DriverStatus driverStatus;
}
