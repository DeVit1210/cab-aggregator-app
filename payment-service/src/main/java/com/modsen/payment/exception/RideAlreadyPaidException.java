package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

import java.time.LocalDateTime;

public class RideAlreadyPaidException extends AlreadyExistsException {
    public RideAlreadyPaidException(Long rideId, LocalDateTime paidAt) {
        super(String.format(MessageTemplates.RIDE_ALREADY_PAID.getValue(), rideId, paidAt));
    }
}
