package com.modsen.driver.exception;

import com.modsen.driver.constants.MessageTemplates;

public class IncorrectPageNumberException extends PageException {
    public IncorrectPageNumberException(int pageNumber) {
        super(String.format(MessageTemplates.INCORRECT_PAGE_NUMBER, pageNumber));
    }

    public IncorrectPageNumberException(int pageSize, int totalPageQuantity, int requestedPageQuantity) {
        super(String.format(
                MessageTemplates.INCORRECT_PAGE_NUMBER_WITH_LIMIT,
                pageSize,
                totalPageQuantity,
                requestedPageQuantity
        ));
    }
}
