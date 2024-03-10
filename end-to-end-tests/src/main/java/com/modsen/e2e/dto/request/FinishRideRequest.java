package com.modsen.e2e.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinishRideRequest {
    private Long id;
    private String paymentType;
}
