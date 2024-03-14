package com.modsen.driver.service.feign;

import com.modsen.driver.config.FeignConfig;
import com.modsen.driver.constants.ServiceMappings;
import com.modsen.driver.dto.response.DriverAccountResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = ServiceMappings.PAYMENT_SERVICE,
        configuration = FeignConfig.class,
        path = ServiceMappings.PAYMENT_BASE_URL
)
@CircuitBreaker(name = "payment-circuit-breaker")
@Retry(name = "payment-retry")
public interface PaymentServiceClient {
    @GetMapping(ServiceMappings.DRIVER_ACCOUNT_BY_ID_URL)
    DriverAccountResponse findAccountById(@PathVariable Long accountId);
}
