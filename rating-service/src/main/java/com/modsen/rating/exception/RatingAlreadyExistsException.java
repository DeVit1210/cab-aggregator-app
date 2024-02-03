package com.modsen.rating.exception;

import com.modsen.rating.constants.MessageTemplates;
import com.modsen.rating.enums.Role;

public class RatingAlreadyExistsException extends RuntimeException {
    public RatingAlreadyExistsException(Role role, Long rideId) {
        super(String.format(
                MessageTemplates.RATING_ALREADY_EXISTS.getValue(),
                role.name(),
                rideId
        ));
    }
}
