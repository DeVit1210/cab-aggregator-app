package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;

public class IncorrectPageSizeException extends PageException {
    public IncorrectPageSizeException(int requestedSize) {
        super(String.format(MessageTemplates.INCORRECT_PAGE_SIZE, requestedSize));
    }
}

