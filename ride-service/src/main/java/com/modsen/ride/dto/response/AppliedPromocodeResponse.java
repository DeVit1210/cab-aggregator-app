package com.modsen.ride.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppliedPromocodeResponse(
        Long id,
        Long passengerId,
        Long promocodeId,
        String promocodeName,
        int discountPercent,
        LocalDateTime appliedAt
) {
}
