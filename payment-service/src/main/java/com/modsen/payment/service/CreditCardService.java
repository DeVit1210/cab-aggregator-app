package com.modsen.payment.service;

import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.response.CreditCardListResponse;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.enums.Role;
import com.modsen.payment.model.CreditCard;

public interface CreditCardService {
    CreditCardResponse createCreditCard(CreditCardRequest request);

    CreditCardListResponse findAllCreditCards(Long cardHolderId, Role role);

    CreditCardResponse findCardById(Long cardId);

    CreditCardResponse setDefaultCreditCard(Long cardId);

    CreditCard findCreditCardByStripeId(String cardStripeId);

    CreditCard findCreditCardById(Long creditCardId);
}
