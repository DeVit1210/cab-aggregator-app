package com.modsen.payment.exception;

import com.modsen.payment.constants.MessageTemplates;

public class PaymentEntityNotFoundException extends RuntimeException {
    public PaymentEntityNotFoundException(Long id, Class<?> entityClass) {
        super(String.format(MessageTemplates.ENTITY_NOT_FOUND.getValue(), entityClass, id));
    }
}
