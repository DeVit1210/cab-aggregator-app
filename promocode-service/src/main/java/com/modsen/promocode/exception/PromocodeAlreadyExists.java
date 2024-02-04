package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;

public class PromocodeAlreadyExists extends RuntimeException {
    public PromocodeAlreadyExists(String promocodeName) {
        super(String.format(MessageTemplates.PROMOCODE_ALREADY_EXISTS.getValue(), promocodeName));
    }
}
