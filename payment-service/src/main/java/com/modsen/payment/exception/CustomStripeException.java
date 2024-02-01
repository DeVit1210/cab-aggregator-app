package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class CustomStripeException extends RuntimeException {
    public CustomStripeException(String code) {
        super(String.format(MessageTemplates.STRIPE_ERROR.getValue(), code));
    }
}
