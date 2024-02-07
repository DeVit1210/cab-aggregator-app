package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;

public class PromocodeAlreadyExistsException extends RuntimeException {
    public PromocodeAlreadyExistsException(String promocodeName) {
        super(String.format(MessageTemplates.PROMOCODE_ALREADY_EXISTS.getValue(), promocodeName));
    }
}
