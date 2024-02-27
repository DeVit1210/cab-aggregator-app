package com.modsen.rating.service.feign;

import com.modsen.rating.config.FeignConfig;
import com.modsen.rating.constants.ServiceMappings;
import com.modsen.rating.dto.response.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = ServiceMappings.RIDE_SERVICE,
        configuration = FeignConfig.class,
        path = ServiceMappings.RIDE_BASE_URL
)
public interface RideServiceClient {
    @GetMapping(ServiceMappings.RIDE_BY_ID_URL)
    RideResponse findRideById(@PathVariable Long rideId);
}
