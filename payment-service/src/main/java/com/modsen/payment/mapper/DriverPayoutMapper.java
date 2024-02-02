package com.modsen.payment.mapper;

import com.modsen.payment.dto.request.DriverPayoutRequest;
import com.modsen.payment.dto.response.DriverPayoutResponse;
import com.modsen.payment.dto.response.Paged;
import com.modsen.payment.model.DriverPayout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverPayoutMapper {
    @Mapping(target = "driverId", source = "driverPayout.account.driverId")
    DriverPayoutResponse toDriverPayoutResponse(DriverPayout driverPayout, BigDecimal leftoverAmount);

    List<DriverPayoutResponse> toDriverPayoutListResponse(List<DriverPayout> driverPayoutList);

    default Paged<DriverPayoutResponse> toPagedDriverPayoutResponse(Page<DriverPayout> driverPayoutPage) {
        return Paged.<DriverPayoutResponse>builder()
                .content(this.toDriverPayoutListResponse(driverPayoutPage.getContent()))
                .pageSize(driverPayoutPage.getSize())
                .totalPageQuantity(driverPayoutPage.getTotalPages())
                .pageNumber(driverPayoutPage.getNumber() + 1)
                .hasPrevious(driverPayoutPage.hasPrevious())
                .hasNext(driverPayoutPage.hasNext())
                .build();
    }
}
