package com.modsen.rating.exception;

import com.modsen.rating.constants.MessageTemplates;
import com.modsen.rating.enums.Role;
import com.modsen.rating.exception.base.BadRequestException;

public class IllegalRatingAttemptException extends BadRequestException {
    public IllegalRatingAttemptException(Long ratedPersonId, Role role) {
        super(String.format(MessageTemplates.ILLEGAL_ID_FOR_RIDE.getValue(), role.name(), ratedPersonId));
    }
}
