package com.modsen.rating.service.feign;

import com.modsen.rating.config.FeignConfig;
import com.modsen.rating.constants.ServiceMappings;
import com.modsen.rating.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = ServiceMappings.PASSENGER_SERVICE,
        configuration = FeignConfig.class,
        url = ServiceMappings.PASSENGER_BASE_URL
)
public interface PassengerServiceClient {
    @GetMapping(ServiceMappings.PASSENGER_BY_ID_URL)
    PassengerResponse findPassengerById(@PathVariable Long passengerId);
}
