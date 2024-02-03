package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class StripeCustomerAlreadyExistsException extends AlreadyExistsException {
    public StripeCustomerAlreadyExistsException(Long passengerId) {
        super(String.format(MessageTemplates.STRIPE_CUSTOMER_ALREADY_EXISTS.getValue(), passengerId));
    }
}
