package com.modsen.passenger.exception;

import com.modsen.passenger.constants.MessageTemplates;

public class IncorrectPageSizeException extends PageException {
    public IncorrectPageSizeException(int requestedSize) {
        super(String.format(MessageTemplates.INCORRECT_PAGE_SIZE, requestedSize));
    }
}
