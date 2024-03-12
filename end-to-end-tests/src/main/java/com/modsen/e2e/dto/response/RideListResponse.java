package com.modsen.e2e.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record RideListResponse(
        List<RideResponse> rides,
        int quantity
) {

}