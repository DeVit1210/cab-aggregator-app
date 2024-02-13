package com.modsen.ride.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RideCostRequest {
    private String pickUpAddress;
    private String destinationAddress;
}
