package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class DefaultCreditCardMissingException extends RuntimeException {
    public DefaultCreditCardMissingException(Long passengerId) {
        super(String.format(MessageTemplates.CREDIT_CARD_MISSING.getValue(), passengerId));
    }
}
