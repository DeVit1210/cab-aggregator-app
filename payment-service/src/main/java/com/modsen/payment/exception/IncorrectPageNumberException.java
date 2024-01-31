package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class IncorrectPageNumberException extends PageException {
    public IncorrectPageNumberException(int pageNumber) {
        super(String.format(MessageTemplates.INCORRECT_PAGE_NUMBER.getValue(), pageNumber));
    }

    public IncorrectPageNumberException(int pageSize, int totalPageQuantity, int requestedPageQuantity) {
        super(String.format(
                MessageTemplates.INCORRECT_PAGE_NUMBER_WITH_LIMIT.getValue(),
                pageSize,
                totalPageQuantity,
                requestedPageQuantity
        ));
    }
}