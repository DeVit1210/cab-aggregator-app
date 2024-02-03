package com.modsen.payment.service.impl;

import com.modsen.payment.dto.request.CustomerRequest;
import com.modsen.payment.dto.response.StripeCustomerResponse;
import com.modsen.payment.exception.PaymentEntityNotFoundException;
import com.modsen.payment.exception.StripeCustomerAlreadyExistsException;
import com.modsen.payment.mapper.StripeCustomerMapper;
import com.modsen.payment.model.StripeCustomer;
import com.modsen.payment.repository.StripeCustomerRepository;
import com.modsen.payment.service.StripeCustomerService;
import com.modsen.payment.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StripeCustomerServiceImpl implements StripeCustomerService {
    private final StripeCustomerRepository customerRepository;
    private final StripeCustomerMapper customerMapper;
    private final StripeService stripeService;

    @Override
    @Transactional
    public StripeCustomerResponse createStripeCustomer(CustomerRequest request) {
        Long id = request.getPassengerId();
        if (customerRepository.existsById(id)) {
            throw new StripeCustomerAlreadyExistsException(id);
        }
        String stripeId = stripeService.createStripeCustomer(request);
        StripeCustomer savedStripeCustomer = customerRepository.save(
                StripeCustomer.builder()
                        .id(id)
                        .stripeCustomerId(stripeId)
                        .build()
        );

        return customerMapper.toStripeCustomerResponse(savedStripeCustomer);
    }

    @Override
    public String getCustomerId(Long passengerId) {
        return customerRepository.findById(passengerId)
                .map(StripeCustomer::getStripeCustomerId)
                .orElseThrow(() -> new PaymentEntityNotFoundException(passengerId, StripeCustomer.class));
    }

    @Override
    public StripeCustomerResponse findStripeCustomerById(Long stripeCustomerId) {
        return customerRepository.findById(stripeCustomerId)
                .map(customerMapper::toStripeCustomerResponse)
                .orElseThrow(() -> new PaymentEntityNotFoundException(stripeCustomerId, StripeCustomer.class));
    }
}
