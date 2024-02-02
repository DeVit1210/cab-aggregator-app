package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class StripeCustomerAlreadyExists extends RuntimeException {
    public StripeCustomerAlreadyExists(Long passengerId) {
        super(String.format(MessageTemplates.STRIPE_CUSTOMER_ALREADY_EXISTS.getValue(), passengerId));
    }
}
