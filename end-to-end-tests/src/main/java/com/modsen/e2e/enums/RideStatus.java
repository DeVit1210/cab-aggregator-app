package com.modsen.e2e.enums;

import java.util.List;

public enum RideStatus {
    WAITING_FOR_DRIVER_CONFIRMATION,
    WITHOUT_DRIVER,
    PENDING,
    ACTIVE,
    FINISHED,
    CANCELED;


    public static List<RideStatus> getConfirmedRideStatusList() {
        return List.of(PENDING, ACTIVE);
    }

    public static List<RideStatus> getNotFinishedStatusList() {
        return List.of(PENDING, ACTIVE, WAITING_FOR_DRIVER_CONFIRMATION, WITHOUT_DRIVER);
    }

    public static List<RideStatus> getNotConfirmedStatusList() {
        return List.of(WAITING_FOR_DRIVER_CONFIRMATION, WITHOUT_DRIVER, PENDING);
    }
}
