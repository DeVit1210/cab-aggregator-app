package com.modsen.e2e.client;

import com.modsen.e2e.dto.response.DriverAccountResponse;
import com.modsen.e2e.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "${feign.client.payment.name}",
        path = "${feign.client.payment.path}"
)
public interface PaymentServiceClient {
    @GetMapping("charges/ride/{rideId}")
    PaymentResponse findPaymentByRid(@PathVariable Long rideId);

    @GetMapping("/driver/accounts/{accountId}")
    DriverAccountResponse findAccountById(@PathVariable Long accountId);
}
