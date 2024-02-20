package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;
import com.modsen.promocode.exception.base.ConflictException;

public class PromocodeAlreadyExistsException extends ConflictException {
    public PromocodeAlreadyExistsException(String promocodeName) {
        super(String.format(MessageTemplates.PROMOCODE_ALREADY_EXISTS.getValue(), promocodeName));
    }
}
