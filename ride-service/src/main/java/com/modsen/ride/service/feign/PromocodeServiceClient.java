package com.modsen.ride.service.feign;

import com.modsen.ride.config.FeignConfig;
import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = ServiceMappings.ServiceName.PROMOCODE_SERVICE,
        configuration = FeignConfig.class,
        url = ServiceMappings.BaseUrl.PROMOCODE_SERVICE
)
public interface PromocodeServiceClient {
    @GetMapping(ServiceMappings.Url.NOT_CONFIRMED_PROMOCODE_FOR_PASSENGER_URL)
    AppliedPromocodeResponse findNotConfirmedPromocode(@PathVariable Long passengerId);

    @PatchMapping(ServiceMappings.Url.CONFIRMED_PROMOCODE_APPLIANCE)
    AppliedPromocodeResponse confirmPromocodeAppliance(@PathVariable Long promocodeId);
}