package com.modsen.driver.enums;

import java.util.List;

public enum DriverStatus {
    HAS_UNCONFIRMED_RIDE,
    ON_WAY_TO_PASSENGER,
    WAITING_FOR_PASSENGER,
    ON_TRIP,
    OFFLINE,
    AVAILABLE;

    public static List<DriverStatus> allowedToBeChangedByDriver() {
        return List.of(OFFLINE, AVAILABLE);
    }
}
