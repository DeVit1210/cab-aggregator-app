package com.modsen.promocode.exception;

import com.modsen.promocode.constants.MessageTemplates;

public class IncorrectSortFieldNameException extends PageException {
    public IncorrectSortFieldNameException(String sortField) {
        super(String.format(MessageTemplates.INCORRECT_SORT_FIELD_NAME.getValue(), sortField));
    }
}
