package com.modsen.ride.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RideListResponse {
    private List<RideResponse> rides;
    private int quantity;

    public static RideListResponse of(List<RideResponse> rides) {
        return new RideListResponse(rides, rides.size());
    }
}
