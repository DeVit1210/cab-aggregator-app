package com.modsen.passenger.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerRequest {
    private String firstName;
    private String secondName;
    private String email;
    private String phoneNumber;
}
