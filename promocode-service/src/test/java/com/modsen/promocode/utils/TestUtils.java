package com.modsen.promocode.utils;

import com.modsen.promocode.constants.TestConstants;
import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.request.UpdateDiscountPercentRequest;
import com.modsen.promocode.dto.response.RideListResponse;
import com.modsen.promocode.enums.ApplianceStatus;
import com.modsen.promocode.model.AppliedPromocode;
import com.modsen.promocode.model.Promocode;
import lombok.experimental.UtilityClass;

import java.util.Collections;

@UtilityClass
public class TestUtils {
    public static Promocode defaultPromocode() {
        return promocodeWithMinRidesAmount(0);
    }

    public static Promocode promocodeWithMinRidesAmount(int ridesAmount) {
        return Promocode.builder()
                .id(TestConstants.PROMOCODE_ID)
                .name(TestConstants.PROMOCODE_NAME)
                .discountPercent(TestConstants.PROMOCODE_DISCOUNT_PERCENT)
                .minRidesAmount(ridesAmount)
                .build();
    }

    public static Promocode promocodeWithMaxUsageAmount(int maxUsageAmount) {
        return Promocode.builder()
                .id(TestConstants.PROMOCODE_ID)
                .name(TestConstants.PROMOCODE_NAME)
                .discountPercent(TestConstants.PROMOCODE_DISCOUNT_PERCENT)
                .maxUsageCount(maxUsageAmount)
                .build();
    }

    public static PromocodeRequest defaultPromocodeRequest() {
        return PromocodeRequest.builder()
                .name(TestConstants.PROMOCODE_NAME)
                .daysQuantity(10)
                .discountPercent(TestConstants.PROMOCODE_DISCOUNT_PERCENT)
                .minRidesQuantity(0)
                .build();
    }

    public static UpdateDiscountPercentRequest defaultUpdateDiscountPercentRequest() {
        return UpdateDiscountPercentRequest.builder()
                .discountPercent(TestConstants.UPDATED_DISCOUNT_PERCENT)
                .promocodeId(TestConstants.PROMOCODE_ID)
                .build();
    }

    public static ApplyPromocodeRequest defaultApplyPromocodeRequest() {
        return ApplyPromocodeRequest.builder()
                .promocodeName(TestConstants.PROMOCODE_NAME)
                .passengerId(TestConstants.PASSENGER_ID)
                .build();
    }

    public static ApplyPromocodeRequest applyPromocodeRequestWithName(String promocodeName) {
        return ApplyPromocodeRequest.builder()
                .promocodeName(promocodeName)
                .passengerId(TestConstants.PASSENGER_ID)
                .build();
    }

    public static RideListResponse emptyRideListResponse() {
        return new RideListResponse(Collections.emptyList(), 0);
    }

    public static AppliedPromocode defaultAppliedPromocode() {
        return AppliedPromocode.builder()
                .id(TestConstants.PROMOCODE_ID)
                .applianceStatus(ApplianceStatus.NOT_CONFIRMED)
                .promocode(defaultPromocode())
                .passengerId(TestConstants.PASSENGER_ID)
                .build();
    }
}
