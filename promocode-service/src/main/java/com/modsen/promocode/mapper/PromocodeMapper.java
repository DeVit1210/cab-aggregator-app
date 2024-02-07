package com.modsen.promocode.mapper;

import com.modsen.promocode.constants.PromocodeConstants;
import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.response.PromocodeResponse;
import com.modsen.promocode.model.Promocode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PromocodeMapper {
    default Promocode toPromocode(PromocodeRequest request) {
        return Promocode.builder()
                .name(request.getName())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(request.getDaysQuantity()))
                .discountPercent(request.getDiscountPercent())
                .minRidesAmount(getMinRidesQuantity(request))
                .maxUsageCount(getMaxUsageCount(request))
                .build();
    }

    PromocodeResponse toPromocodeResponse(Promocode promocode);

    List<PromocodeResponse> toPromocodeListResponse(List<Promocode> promocodeList);

    private int getMinRidesQuantity(PromocodeRequest request) {
        return Optional.of(request.getMinRidesQuantity())
                .orElse(PromocodeConstants.DEFAULT_MIN_RIDES_QUANTITY);
    }

    private int getMaxUsageCount(PromocodeRequest request) {
        return Optional.of(request.getMaxUsageCount())
                .orElse(PromocodeConstants.DEFAULT_MAX_USAGE_COUNT);
    }
}
