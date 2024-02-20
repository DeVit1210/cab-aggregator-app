package com.modsen.ride.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequest {
    private Long passengerId;
    private BigDecimal amount;
    private String type;
    private Long rideId;
    private Long driverId;
}