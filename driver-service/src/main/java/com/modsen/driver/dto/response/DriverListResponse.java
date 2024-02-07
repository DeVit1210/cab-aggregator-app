package com.modsen.driver.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverListResponse {
    private List<DriverResponse> drivers;
    private int quantity;

    public static DriverListResponse of(List<DriverResponse> drivers) {
        return new DriverListResponse(drivers, drivers.size());
    }
}
