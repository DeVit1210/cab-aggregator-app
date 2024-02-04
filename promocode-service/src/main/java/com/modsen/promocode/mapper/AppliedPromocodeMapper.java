package com.modsen.promocode.mapper;

import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;
import com.modsen.promocode.model.AppliedPromocode;
import com.modsen.promocode.model.Promocode;
import org.springframework.stereotype.Component;

@Component
public class AppliedPromocodeMapper {
    public AppliedPromocode toAppliedPromocode(Promocode actualPromocode, ApplyPromocodeRequest request) {
        return AppliedPromocode.builder()
                .promocode(actualPromocode)
                .passengerId(request.getPassengerId())
                .build();
    }

    public AppliedPromocodeResponse toAppliedPromocodeResponse(Promocode actualPromocode,
                                                               AppliedPromocode appliedPromocode) {
        return AppliedPromocodeResponse.builder()
                .id(appliedPromocode.getId())
                .promocodeId(actualPromocode.getId())
                .promocodeName(actualPromocode.getName())
                .discountPercent(actualPromocode.getDiscountPercent())
                .passengerId(appliedPromocode.getPassengerId())
                .appliedAt(appliedPromocode.getAppliedAt())
                .build();
    }
}
