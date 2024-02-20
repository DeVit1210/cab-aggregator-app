package com.modsen.ride.dto.response;


import com.modsen.ride.enums.Role;
import lombok.Builder;

@Builder
public record CreditCardResponse(
        Long id,
        Long cardHolderId,
        String number,
        Role role
) {
}
