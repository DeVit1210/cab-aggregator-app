package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;
import com.modsen.payment.enums.Role;

public class InvalidCreditCardHolderException extends RuntimeException {
    public InvalidCreditCardHolderException(Long creditCardId, Long driverId, Role role) {
        super(String.format(
                MessageTemplates.CREDIT_CARD_INVALID_HOLDER.getValue(),
                creditCardId,
                role.name(),
                driverId
        ));
    }
}
