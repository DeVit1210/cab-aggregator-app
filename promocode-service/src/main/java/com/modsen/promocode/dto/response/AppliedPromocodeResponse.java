package com.modsen.promocode.dto.response;

import com.modsen.promocode.enums.ApplianceStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppliedPromocodeResponse(
        Long id,
        Long passengerId,
        Long promocodeId,
        String promocodeName,
        int discountPercent,
        LocalDateTime appliedAt,
        ApplianceStatus applianceStatus
) {
}
