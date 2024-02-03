package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.enums.Role;

public class CreditCardAlreadyExistsException extends AlreadyExistsException {
    public CreditCardAlreadyExistsException(Role role, Long cardHolderId, String cardNumber) {
        super(String.format(
                MessageTemplates.CREDIT_CARD_ALREADY_EXISTS.getValue(),
                cardNumber,
                role.name(),
                cardHolderId
        ));
    }
}
