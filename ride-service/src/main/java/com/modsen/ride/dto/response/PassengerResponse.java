package com.modsen.ride.dto.response;

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