package com.modsen.payment.service.impl;

import com.modsen.payment.service.StripeCustomerService;
import org.springframework.stereotype.Service;

@Service
public class StripeCustomerServiceImpl implements StripeCustomerService {
    @Override
    public String getCustomerId(Long passengerId) {
        return null;
    }
}
