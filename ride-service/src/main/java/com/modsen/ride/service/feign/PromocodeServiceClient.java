package com.modsen.ride.service.feign;

import com.modsen.ride.constants.ServiceMappings;
import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import com.modsen.ride.service.feign.fallback.PromocodeServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(
        name = "${feign.client.promocode.name}",
        path = "${feign.client.promocode.url}",
        fallback = PromocodeServiceClientFallback.class
)
public interface PromocodeServiceClient {
    @GetMapping(ServiceMappings.Url.NOT_CONFIRMED_PROMOCODE_FOR_PASSENGER_URL)
    AppliedPromocodeResponse findNotConfirmedPromocode(@PathVariable Long passengerId);

    @PutMapping(ServiceMappings.Url.CONFIRMED_PROMOCODE_APPLIANCE)
    AppliedPromocodeResponse confirmPromocodeAppliance(@PathVariable Long promocodeId);
}