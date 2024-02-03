package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class PaymentEntityNotFoundException extends EntityNotFoundException {
    public PaymentEntityNotFoundException(Long id, Class<?> entityClass) {
        super(String.format(MessageTemplates.ENTITY_NOT_FOUND_BY_ID.getValue(), entityClass.getSimpleName(), id));
    }

    public PaymentEntityNotFoundException(String stripeId, Class<?> entityClass) {
        super(String.format(MessageTemplates.ENTITY_NOT_FOUND_BY_STRIPE_ID.getValue(), entityClass.getSimpleName(), stripeId));
    }
}
