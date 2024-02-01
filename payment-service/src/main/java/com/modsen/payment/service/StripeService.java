package com.modsen.payment.service;

import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.request.CustomerRequest;

import java.math.BigDecimal;

public interface StripeService {
    String createTokenForCreditCard(CreditCardRequest request);

    String createStripeCustomer(CustomerRequest request);

    void setDefaultCreditCard(String stripeCustomerId, String creditCardToken);

    String createPayment(String stripeCustomerId, BigDecimal amount);
}
