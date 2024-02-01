package com.modsen.payment.dto.request;

import com.modsen.payment.constants.RegexConstants;
import com.modsen.payment.constants.ValidationConstants;
import com.modsen.payment.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
public class CreditCardRequest {
//    private Long cardHolderId;
//    private Role role;
    @NotEmpty(message = ValidationConstants.CARD_NUMBER_NOT_EMPTY)
//    @Pattern(regexp = RegexConstants.CARD_NUMBER_PATTERN, message = ValidationConstants.CARD_NUMBER_INVALID)
    private String number;
//    @Range(min = 1, max = 12, message = ValidationConstants.EXPIRE_MONTH_INVALID)
    private int expireMonth;
//    @Range(min = 2024, message = ValidationConstants.EXPIRE_YEAR_INVALID)
    private int expireYear;
//    @Pattern(regexp = RegexConstants.CVC_PATTERN, message = ValidationConstants.CVC_INVALID)
    private int cvc;
//    private boolean isDefault;
}
