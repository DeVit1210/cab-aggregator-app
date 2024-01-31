package com.modsen.payment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditCardResponse {
    private Long id;
    private Long cardHolderId;
    private String cardNumber;
}
