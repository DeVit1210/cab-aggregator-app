package com.modsen.payment.service;


public interface StripeCustomerService {
    String getCustomerId(Long passengerId);
}
