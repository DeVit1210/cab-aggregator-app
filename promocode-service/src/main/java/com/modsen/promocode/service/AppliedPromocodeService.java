package com.modsen.promocode.service;

import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;

public interface AppliedPromocodeService {
    AppliedPromocodeResponse applyPromocode(ApplyPromocodeRequest request);

    AppliedPromocodeResponse findNotConfirmedAppliedPromocode(Long passengerId);

    AppliedPromocodeResponse confirmAppliedPromocode(Long appliedPromocodeId);

    AppliedPromocodeResponse findAppliedPromocodeById(Long appliedPromocodeId);
}
