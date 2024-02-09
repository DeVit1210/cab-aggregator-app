package com.modsen.promocode.mapper;

import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;
import com.modsen.promocode.model.AppliedPromocode;
import com.modsen.promocode.model.Promocode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppliedPromocodeMapper {
    default AppliedPromocode toAppliedPromocode(Promocode actualPromocode, ApplyPromocodeRequest request) {
        return AppliedPromocode.builder()
                .promocode(actualPromocode)
                .passengerId(request.getPassengerId())
                .build();
    }

    @Mapping(target = "id", source = "appliedPromocode.id")
    @Mapping(target = "promocodeId", source = "actualPromocode.id")
    @Mapping(target = "promocodeName", source = "actualPromocode.name")
    AppliedPromocodeResponse toAppliedPromocodeResponse(Promocode actualPromocode, AppliedPromocode appliedPromocode);
}
