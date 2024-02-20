package com.modsen.passenger.exception;


import com.modsen.passenger.constants.MessageTemplates;

public class UniqueConstraintViolationException extends BadRequestException {
    public UniqueConstraintViolationException(MessageTemplates messageTemplate, String invalidValue) {
        super(String.format(messageTemplate.getValue(), invalidValue));
    }
}
