package com.modsen.payment.controller;

import com.modsen.payment.constants.ControllerMappings;
import com.modsen.payment.dto.request.CustomerRequest;
import com.modsen.payment.dto.response.StripeCustomerResponse;
import com.modsen.payment.service.StripeCustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerMappings.STRIPE_CUSTOMER_CONTROLLER)
@RequiredArgsConstructor
public class StripeCustomerController {
    private final StripeCustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StripeCustomerResponse createStripeCustomer(@Valid @RequestBody CustomerRequest request) {
        return customerService.createStripeCustomer(request);
    }

    @GetMapping("/{customerId}")
    public StripeCustomerResponse findStripeCustomerById(@PathVariable Long customerId) {
        return customerService.findStripeCustomerById(customerId);
    }
}
