package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;

public class IncorrectPageSizeException extends PageException {
    public IncorrectPageSizeException(int requestedSize) {
        super(String.format(MessageTemplates.INCORRECT_PAGE_SIZE.getValue(), requestedSize));
    }
}


