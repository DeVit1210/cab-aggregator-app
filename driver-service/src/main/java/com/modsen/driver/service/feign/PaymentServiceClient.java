package com.modsen.driver.service.feign;

import com.modsen.driver.dto.response.DriverAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("PAYMENT-SERVICE")
public interface PaymentServiceClient {
    @GetMapping("/api/v1/driver/accounts/{accountId}")
    DriverAccountResponse findAccountById(@PathVariable Long accountId);
}
