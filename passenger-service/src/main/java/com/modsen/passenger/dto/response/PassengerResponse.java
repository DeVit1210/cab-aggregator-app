package com.modsen.passenger.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
