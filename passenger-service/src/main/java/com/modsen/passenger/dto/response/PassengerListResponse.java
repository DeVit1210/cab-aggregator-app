package com.modsen.passenger.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PassengerListResponse {
    private List<PassengerResponse> passengers;
}
