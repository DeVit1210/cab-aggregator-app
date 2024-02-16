package com.modsen.promocode.service;

import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.request.UpdateDiscountPercentRequest;
import com.modsen.promocode.dto.response.PromocodeListResponse;
import com.modsen.promocode.dto.response.PromocodeResponse;
import com.modsen.promocode.model.Promocode;

public interface PromocodeService {
    PromocodeListResponse findAllPromocodes();

    PromocodeResponse findPromocodeById(Long promocodeId);

    PromocodeResponse createPromocode(PromocodeRequest request);

    PromocodeResponse updatePromocode(UpdateDiscountPercentRequest request);

    Promocode findByName(String promocodeName);

    void deletePromocode(Long promocodeId);
}
