package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.enums.Role;

public class CardIsNotDefaultException extends RuntimeException {
    public CardIsNotDefaultException(Role role, Long cardHolderId, String cardNumber) {
        super(String.format(
                MessageTemplates.DEFAULT_CREDIT_CARD.getValue(),
                cardNumber,
                role.name(),
                cardHolderId
        ));
    }
}
