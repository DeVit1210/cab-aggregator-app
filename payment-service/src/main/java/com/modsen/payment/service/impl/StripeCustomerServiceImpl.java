package com.modsen.payment.service.impl;

import com.modsen.payment.dto.request.CustomerRequest;
import com.modsen.payment.dto.response.StripeCustomerResponse;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.exception.StripeCustomerAlreadyExists;
import com.modsen.payment.mapper.StripeCustomerMapper;
import com.modsen.payment.model.StripeCustomer;
import com.modsen.payment.repository.StripeCustomerRepository;
import com.modsen.payment.service.StripeCustomerService;
import com.modsen.payment.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeCustomerServiceImpl implements StripeCustomerService {
    private final StripeCustomerRepository customerRepository;
    private final StripeCustomerMapper customerMapper;
    private final StripeService stripeService;
    @Override
    public String getCustomerId(Long passengerId) {
        return customerRepository.findByPassengerId(passengerId)
                .map(StripeCustomer::getStripeId)
                .orElseThrow(() -> new PaymentEntityNotFoundException(passengerId, StripeCustomer.class));
    }

    @Override
    public StripeCustomerResponse createStripeCustomer(CustomerRequest request) {
        Long passengerId = request.getPassengerId();
        if (customerRepository.existsById(passengerId)) {
            throw new StripeCustomerAlreadyExists(passengerId);
        }
        String stripeId = stripeService.createStripeCustomer(request);
        StripeCustomer savedStripeCustomer = customerRepository.save(
                StripeCustomer.builder()
                    .stripeId(stripeId)
                    .passengerId(passengerId)
                    .build()
        );

        return customerMapper.toStripeCustomerResponse(savedStripeCustomer);
    }
}
