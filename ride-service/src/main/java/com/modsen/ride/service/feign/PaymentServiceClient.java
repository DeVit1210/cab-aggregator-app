package com.modsen.ride.service.feign;

import com.modsen.ride.config.FeignConfig;
import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.request.PaymentRequest;
import com.modsen.ride.dto.response.CreditCardResponse;
import com.modsen.ride.dto.response.PaymentResponse;
import com.modsen.ride.dto.response.StripeCustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = ServiceMappings.ServiceName.PAYMENT_SERVICE,
        configuration = FeignConfig.class,
        url = ServiceMappings.BaseUrl.PAYMENT_SERVICE
)
public interface PaymentServiceClient {
    @GetMapping(ServiceMappings.Url.STRIPE_CUSTOMER_BY_ID_URL)
    StripeCustomerResponse findStripeCustomerById(@PathVariable Long customerId);

    @GetMapping(ServiceMappings.Url.DEFAULT_CARD_FOR_PASSENGER_URL)
    CreditCardResponse getDefaultCreditCard(@PathVariable Long passengerId);

    @PostMapping
    PaymentResponse createPayment(@RequestBody PaymentRequest request);
}
