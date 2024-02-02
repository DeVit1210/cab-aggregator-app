package com.modsen.payment.service;


import com.modsen.payment.dto.request.CustomerRequest;
import com.modsen.payment.dto.response.StripeCustomerResponse;

public interface StripeCustomerService {
    String getCustomerId(Long passengerId);
    StripeCustomerResponse createStripeCustomer(CustomerRequest request);
}
