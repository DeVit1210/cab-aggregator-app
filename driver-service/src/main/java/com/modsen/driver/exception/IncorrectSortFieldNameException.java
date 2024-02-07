package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;

public class IncorrectSortFieldNameException extends PageException {
    public IncorrectSortFieldNameException(String sortField) {
        super(String.format(MessageTemplates.INCORRECT_SORT_FIELD_NAME.getValue(), sortField));
    }
}
