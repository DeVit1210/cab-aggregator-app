package com.modsen.ride.constants;

public interface ExceptionConstants {
    String WAITING_FOR_CONFIRMATION_STATE_REQUIRED =
            "Ride need to be in WAITING_FOR_DRIVER_CONFIRMATION state if you want to accept the ride";
    String PENDING_STATUS_REQUIRED =
            "Ride need to be in PENDING state if you want to notify passenger about driver arrival!";
    String ACTIVE_STATUS_REQUIRED =
            "Ride need to be in ACTIVE state if you want to finish the ride!";
    String NOT_CONFIRMED_STATUS_REQUIRED =
            "Ride need to be not confirmed if you want to cancel it!";
    String FINISHED_STATUS_REQUIRED =
            "Ride need to be in FINISHED state if you want to count its total ride time!";
    String CANCELED_STATUS_REQUIRED =
            "Ride need to be in CANCELED state if you want to delete it!";
}
