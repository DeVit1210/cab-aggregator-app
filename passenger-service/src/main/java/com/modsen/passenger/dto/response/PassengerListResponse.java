package com.modsen.passenger.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PassengerListResponse {
    private List<PassengerResponse> passengers;
    private int quantity;

    public static PassengerListResponse of(List<PassengerResponse> passengers) {
        return new PassengerListResponse(passengers, passengers.size());
    }
}
