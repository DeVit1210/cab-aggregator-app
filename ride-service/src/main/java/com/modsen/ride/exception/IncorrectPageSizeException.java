package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;

public class IncorrectPageSizeException extends PageException {
    public IncorrectPageSizeException(int requestedSize) {
        super(String.format(MessageTemplates.INCORRECT_PAGE_SIZE.getValue(), requestedSize));
    }
}