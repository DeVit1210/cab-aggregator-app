package com.modsen.promocode.dto.request;

import com.modsen.promocode.constants.ValidationConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
public class UpdateDiscountPercentRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long promocodeId;
    @Range(min = 1, max = 99, message = ValidationConstants.DISCOUNT_PERCENT_INVALID)
    private int discountPercent;
}
