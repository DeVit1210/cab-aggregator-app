package com.modsen.driver.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RideRequest {
    private Long id;
    private Long passengerId;
    private String pickUpAddress;
    private String destinationAddress;
    private BigDecimal cost;
}
