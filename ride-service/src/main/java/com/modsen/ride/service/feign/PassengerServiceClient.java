package com.modsen.ride.service.feign;

import com.modsen.ride.config.FeignConfig;
import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "${feign.client.passenger.name}",
        configuration = FeignConfig.class,
        url = "${feign.client.passenger.url}"
)
public interface PassengerServiceClient {
    @GetMapping(ServiceMappings.Url.PASSENGER_BY_ID_URL)
    PassengerResponse findPassengerById(@PathVariable Long passengerId);
}
