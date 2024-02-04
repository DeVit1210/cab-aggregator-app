package com.modsen.promocode.service;

import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;
import com.modsen.promocode.dto.response.PromocodeListResponse;
import com.modsen.promocode.dto.response.PromocodeResponse;

public interface PromocodeService {
    PromocodeListResponse findAllPromocodes();

    PromocodeResponse findPromocodeById(PromocodeRequest request);

    PromocodeResponse createPromocode(PromocodeRequest request);

    PromocodeResponse updatePromocode(PromocodeRequest request);

    AppliedPromocodeResponse applyPromocode(ApplyPromocodeRequest request);

    void deletePromocode(Long promocodeId);
}
