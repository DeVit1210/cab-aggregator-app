package com.modsen.driver.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RideResponse {
    private Long id;
    private Long passengerId;
    private String pickUpAddress;
    private String destinationAddress;
    private BigDecimal cost;
    private boolean isDriverAvailable;
    private Long driverId;
}
