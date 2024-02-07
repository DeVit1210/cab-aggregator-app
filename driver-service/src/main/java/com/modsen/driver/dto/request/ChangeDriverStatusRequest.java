package com.modsen.driver.dto.request;

import com.modsen.driver.enums.DriverStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeDriverStatusRequest {
    private long driverId;
    private DriverStatus newStatus;
}
