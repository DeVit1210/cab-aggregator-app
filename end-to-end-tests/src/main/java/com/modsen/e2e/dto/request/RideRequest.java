package com.modsen.e2e.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RideRequest {
    private Long passengerId;
    private String pickUpAddress;
    private String destinationAddress;
    private BigDecimal rideCost;
}