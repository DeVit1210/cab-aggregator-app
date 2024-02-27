package com.modsen.ride.service.feign.fallback;

import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import com.modsen.ride.service.feign.PromocodeServiceClient;
import org.springframework.stereotype.Component;

@Component
public class PromocodeServiceClientFallback implements PromocodeServiceClient {
    @Override
    public AppliedPromocodeResponse findNotConfirmedPromocode(Long passengerId) {
        return AppliedPromocodeResponse.empty();
    }

    @Override
    public AppliedPromocodeResponse confirmPromocodeAppliance(Long promocodeId) {
        return AppliedPromocodeResponse.empty();
    }
}
