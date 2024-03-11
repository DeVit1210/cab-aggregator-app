package com.modsen.e2e.client;

import com.modsen.e2e.dto.response.AppliedPromocodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "${feign.client.promocode.name}",
        path = "${feign.client.promocode.path}"
)
public interface PromocodeServiceClient {
    @GetMapping("/appliance/not-confirmed")
    AppliedPromocodeResponse findNotConfirmedPromocode(@RequestParam Long passengerId);

    @GetMapping("/appliance/{appliedPromocodeId}")
    AppliedPromocodeResponse findAppliedPromocodeById(@PathVariable Long appliedPromocodeId);
}
