package com.modsen.ride.enums;

import java.util.List;

public enum RideStatus {
    WAITING_FOR_DRIVER_CONFIRMATION,
    PENDING,
    ACTIVE,
    FINISHED,
    CANCELED;

    public static List<RideStatus> getConfirmedRideStatusList() {
        return List.of(PENDING, ACTIVE);
    }
}
