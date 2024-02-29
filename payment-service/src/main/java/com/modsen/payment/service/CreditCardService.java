package com.modsen.payment.service;

import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.response.CreditCardListResponse;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.model.CreditCard;

public interface CreditCardService {
    CreditCardResponse createCreditCard(CreditCardRequest request);

    CreditCardListResponse findAllCreditCards(Long cardHolderId, String role);

    CreditCardResponse findCardById(Long cardId);

    CreditCardResponse setDefaultCreditCard(Long cardId);

    CreditCard getDefaultCreditCard(Long cardHolderId);

    CreditCard findCreditCardByStripeId(String cardStripeId);
}
