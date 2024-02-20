package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;
import com.modsen.promocode.exception.base.NotFoundException;

public class PromocodeNotFoundException extends NotFoundException {
    public PromocodeNotFoundException(Long promocodeId) {
        super(String.format(MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(), promocodeId));
    }

    public PromocodeNotFoundException(String promocodeName) {
        super(String.format(MessageTemplates.PROMOCODE_NOT_FOUND_BY_NAME.getValue(), promocodeName));
    }
}
