package com.modsen.rating.exception;

import com.modsen.rating.constants.MessageTemplates;
import com.modsen.rating.enums.Role;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(Long id, Role role) {
        super(String.format(resolveMessageTemplate(role), id));
    }

    private static String resolveMessageTemplate(Role role) {
        return role.equals(Role.DRIVER)
                ? MessageTemplates.RATING_NOT_FOUND_FOR_DRIVER.getValue()
                : MessageTemplates.RATING_NOT_FOUND_FOR_PASSENGER.getValue();
    }
}
