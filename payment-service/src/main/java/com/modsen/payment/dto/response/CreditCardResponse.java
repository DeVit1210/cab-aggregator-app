package com.modsen.payment.dto.response;

import com.modsen.payment.enums.Role;
import lombok.Builder;

@Builder
public record CreditCardResponse(
        Long id,
        Long cardHolderId,
        String number,
        Role role
) {
}
