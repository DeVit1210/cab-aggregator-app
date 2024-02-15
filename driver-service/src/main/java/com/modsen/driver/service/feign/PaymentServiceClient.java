package com.modsen.driver.service.feign;

import com.modsen.driver.config.FeignConfig;
import com.modsen.driver.constants.ServiceMappings;
import com.modsen.driver.dto.response.DriverAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = ServiceMappings.PAYMENT_SERVICE,
        configuration = FeignConfig.class,
        url = ServiceMappings.PAYMENT_BASE_URL
)
public interface PaymentServiceClient {
    @GetMapping(ServiceMappings.DRIVER_ACCOUNT_BY_ID_URL)
    DriverAccountResponse findAccountById(@PathVariable Long accountId);
}
