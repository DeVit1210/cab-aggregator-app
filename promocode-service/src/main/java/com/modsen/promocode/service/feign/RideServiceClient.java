package com.modsen.promocode.service.feign;

import com.modsen.promocode.config.FeignConfig;
import com.modsen.promocode.constants.ServiceMappings;
import com.modsen.promocode.dto.response.RideListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = ServiceMappings.RIDE_SERVICE,
        configuration = FeignConfig.class,
        url = ServiceMappings.RIDE_BASE_URL
)
public interface RideServiceClient {
    @GetMapping
    RideListResponse findAllRidesForPerson(@RequestParam Long personId, @RequestParam String role);
}
