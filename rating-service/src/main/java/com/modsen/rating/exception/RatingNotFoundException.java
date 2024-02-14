package com.modsen.rating.exception;

import com.modsen.rating.constants.MessageTemplates;
import com.modsen.rating.enums.Role;
import com.modsen.rating.exception.base.NotFoundException;

public class RatingNotFoundException extends NotFoundException {
    public RatingNotFoundException(Long id, Role role) {
        super(String.format(resolveMessageTemplate(role), id));
    }

    public RatingNotFoundException(Long ratingId) {
        super(String.format(MessageTemplates.RATING_NOT_FOUND.getValue(), ratingId));
    }

    private static String resolveMessageTemplate(Role role) {
        return role.equals(Role.DRIVER)
                ? MessageTemplates.RATING_NOT_FOUND_FOR_DRIVER.getValue()
                : MessageTemplates.RATING_NOT_FOUND_FOR_PASSENGER.getValue();
    }
}
