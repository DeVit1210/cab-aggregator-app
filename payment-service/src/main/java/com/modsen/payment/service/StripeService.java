package com.modsen.payment.service;

import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.model.Payment;

public interface StripeService {
    String createTokenForCreditCard(CreditCardRequest request);

    String createStripeCustomer(Long passengerId);

    void changeDefaultCreditCard(String stripeCustomerId, String creditCardToken);

    void setDefaultCreditCard(String stripeCustomerId, String creditCardToken);

    Payment createPayment(PaymentRequest request);
}
