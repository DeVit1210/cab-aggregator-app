package com.modsen.ride.service.feign;

import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("PROMOCODE-SERVICE")
public interface PromocodeServiceClient {
    @GetMapping("/api/v1/promocode/appliance/{passengerId}")
    AppliedPromocodeResponse findNotConfirmedPromocode(@PathVariable Long passengerId);

    @PatchMapping("/api/v1/promocode/appliance/{promocodeId}")
    AppliedPromocodeResponse confirmPromocodeAppliance(@PathVariable Long promocodeId);
}
