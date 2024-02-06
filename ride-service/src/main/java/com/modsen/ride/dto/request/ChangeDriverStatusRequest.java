package com.modsen.ride.dto.request;

import com.modsen.ride.enums.DriverStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeDriverStatusRequest {
    private Long driverId;
    private DriverStatus driverStatus;
}
