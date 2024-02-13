package com.modsen.rating.dto.response;

import lombok.Builder;

@Builder
public record PassengerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}