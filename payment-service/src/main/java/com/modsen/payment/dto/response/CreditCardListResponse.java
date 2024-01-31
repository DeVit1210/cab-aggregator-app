package com.modsen.payment.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditCardListResponse {
    private List<CreditCardResponse> creditCards;
    private int quantity;

    public static CreditCardListResponse of(List<CreditCardResponse> creditCards) {
        return new CreditCardListResponse(creditCards, creditCards.size());
    }
}
