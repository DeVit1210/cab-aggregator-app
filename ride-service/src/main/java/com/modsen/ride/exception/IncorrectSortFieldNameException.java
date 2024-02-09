package com.modsen.ride.exception;

import com.modsen.ride.constants.MessageTemplates;

public class IncorrectSortFieldNameException extends PageException {
    public IncorrectSortFieldNameException(String sortField) {
        super(String.format(MessageTemplates.INCORRECT_SORT_FIELD_NAME.getValue(), sortField));
    }
}

