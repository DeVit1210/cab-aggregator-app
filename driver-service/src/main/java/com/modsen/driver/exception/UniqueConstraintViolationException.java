package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;
import com.modsen.driver.exception.base.BadRequestException;
import jakarta.validation.constraints.Email;

public class UniqueConstraintViolationException extends BadRequestException {
    public UniqueConstraintViolationException(MessageTemplates messageTemplate, String invalidValue) {
        super(String.format(messageTemplate.getValue(), invalidValue));
    }
}
