package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class IncorrectPageSizeException extends PageException {
    public IncorrectPageSizeException(int requestedSize) {
        super(String.format(MessageTemplates.INCORRECT_PAGE_SIZE.getValue(), requestedSize));
    }
}
