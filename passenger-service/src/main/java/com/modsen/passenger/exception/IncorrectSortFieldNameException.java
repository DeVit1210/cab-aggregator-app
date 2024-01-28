package com.modsen.passenger.exception;

import com.modsen.passenger.constants.MessageTemplates;

public class IncorrectSortFieldNameException extends PageException {
    public IncorrectSortFieldNameException(String sortField) {
        super(String.format(MessageTemplates.INCORRECT_SORT_FIELD_NAME, sortField));
    }
}
