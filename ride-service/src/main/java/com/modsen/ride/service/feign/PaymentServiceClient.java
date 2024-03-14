package com.modsen.ride.service.feign;

import com.modsen.ride.config.FeignConfig;
import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.request.PaymentRequest;
import com.modsen.ride.dto.response.CreditCardResponse;
import com.modsen.ride.dto.response.PaymentResponse;
import com.modsen.ride.dto.response.StripeCustomerResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(
        name = "${feign.client.payment.name}",
        configuration = FeignConfig.class,
        path = "${feign.client.payment.url}"
)
@CircuitBreaker(name = "payment-circuit-breaker")
@Retry(name = "payment-retry")
public interface PaymentServiceClient {
    @GetMapping(ServiceMappings.Url.STRIPE_CUSTOMER_BY_ID_URL)
    StripeCustomerResponse findStripeCustomerById(@PathVariable Long customerId);

    @GetMapping(ServiceMappings.Url.DEFAULT_CARD_FOR_PASSENGER_URL)
    CreditCardResponse getDefaultCreditCard(@PathVariable Long passengerId);

    @PostMapping(ServiceMappings.Url.CREATE_PAYMENT_URL)
    @ResponseStatus(HttpStatus.CREATED)
    PaymentResponse createPayment(@RequestBody @Valid PaymentRequest request);
}
