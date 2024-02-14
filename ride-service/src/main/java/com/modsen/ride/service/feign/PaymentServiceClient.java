package com.modsen.ride.service.feign;

import com.modsen.ride.dto.response.CreditCardResponse;
import com.modsen.ride.dto.response.StripeCustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("PAYMENT-SERVICE")
public interface PaymentServiceClient {
    @GetMapping("/api/v1/customers/{customerId}")
    StripeCustomerResponse findStripeCustomerById(@PathVariable Long customerId);

    @GetMapping("/api/v1/credit-cards/{passengerId}")
    CreditCardResponse getDefaultCreditCard(@PathVariable Long passengerId);
}
