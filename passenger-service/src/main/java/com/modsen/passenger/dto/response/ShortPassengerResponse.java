package com.modsen.passenger.dto.response;

import lombok.Builder;

@Builder
public record ShortPassengerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
