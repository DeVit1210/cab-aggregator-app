package com.modsen.ride.enums;

import com.modsen.ride.constants.ExceptionConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum RideStatus {
    WAITING_FOR_DRIVER_CONFIRMATION(ExceptionConstants.WAITING_FOR_CONFIRMATION_STATE_REQUIRED),
    WITHOUT_DRIVER(ExceptionConstants.NOT_CONFIRMED_STATUS_REQUIRED),
    PENDING(ExceptionConstants.PENDING_STATUS_REQUIRED),
    ACTIVE(ExceptionConstants.ACTIVE_STATUS_REQUIRED),
    FINISHED(ExceptionConstants.FINISHED_STATUS_REQUIRED),
    CANCELED(ExceptionConstants.CANCELED_STATUS_REQUIRED);


    private final String exceptionMessage;

    public static List<RideStatus> getConfirmedRideStatusList() {
        return List.of(PENDING, ACTIVE);
    }
}
