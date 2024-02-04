package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;

public class PromocodeNotFoundException extends RuntimeException {
    public PromocodeNotFoundException(Long promocodeId) {
        super(String.format(MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(), promocodeId));
    }

    public PromocodeNotFoundException(String promocodeName) {
        super(String.format(MessageTemplates.PROMOCODE_NOT_FOUND_BY_NAME.getValue(), promocodeName));
    }
}
