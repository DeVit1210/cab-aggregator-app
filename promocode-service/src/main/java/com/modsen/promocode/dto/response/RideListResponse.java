package com.modsen.promocode.dto.response;

import java.util.List;

public record RideListResponse(
        List<RideResponse> rides,
        int quantity
) {
}
