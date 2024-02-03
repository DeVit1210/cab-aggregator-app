package com.modsen.rating.exception;

import com.modsen.rating.constants.MessageTemplates;

public class IncorrectPageSizeException extends PageException {
    public IncorrectPageSizeException(int requestedSize) {
        super(String.format(MessageTemplates.INCORRECT_PAGE_SIZE.getValue(), requestedSize));
    }
}

