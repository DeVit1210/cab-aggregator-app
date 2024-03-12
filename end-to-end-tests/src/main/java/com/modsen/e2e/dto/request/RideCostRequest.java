package com.modsen.e2e.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RideCostRequest {
    private Long passengerId;
    private String pickUpAddress;
    private String destinationAddress;
}
