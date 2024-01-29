package com.modsen.driver.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverResponseList {
    private List<DriverResponse> drivers;
    private int quantity;

    public static DriverResponseList of(List<DriverResponse> drivers) {
        return new DriverResponseList(drivers, drivers.size());
    }
}
