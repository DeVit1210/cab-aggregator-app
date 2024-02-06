package com.modsen.ride.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindDriverRequest {
    private Long rideId;
}
