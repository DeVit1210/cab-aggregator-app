package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class IncorrectSortFieldNameException extends PageException {
    public IncorrectSortFieldNameException(String sortField, Class<?> entityClass) {
        super(String.format(
                MessageTemplates.INCORRECT_SORT_FIELD_NAME.getValue(),
                sortField,
                entityClass.getSimpleName()
        ));
    }
}
