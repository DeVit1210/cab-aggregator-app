package com.modsen.e2e.dto.response;

import com.modsen.e2e.enums.ApplianceStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppliedPromocodeResponse(
        Long id,
        Long passengerId,
        Long promocodeId,
        String promocodeName,
        int discountPercent,
        ApplianceStatus applianceStatus,
        LocalDateTime appliedAt
) {
}
