package com.modsen.payment.dto.response;

import java.util.List;

public record CreditCardListResponse(
        List<CreditCardResponse> creditCards,
        int quantity
) {
    public static CreditCardListResponse of(List<CreditCardResponse> creditCards) {
        return new CreditCardListResponse(creditCards, creditCards.size());
    }
}
