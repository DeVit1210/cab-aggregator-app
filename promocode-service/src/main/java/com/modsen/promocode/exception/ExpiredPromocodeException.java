package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;

public class ExpiredPromocodeException extends RuntimeException {
    public ExpiredPromocodeException(String promocodeName) {
        super(String.format(MessageTemplates.EXPIRED_PROMOCODE.getValue(), promocodeName));
    }
}
